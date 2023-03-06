package xyz.qwewqa.relive.simulator.core.stage.strategy.complete

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.*
import com.github.h0tk3y.betterParse.parser.ParseResult
import com.github.h0tk3y.betterParse.parser.Parsed
import com.github.h0tk3y.betterParse.parser.Parser

enum class NumericalInfixOperator {
  PLUS,
  MINUS,
  DIV,
  TIMES,
  MOD,
}

enum class BooleanInfixOperator {
  OR,
  AND,
}

enum class ComparisonOperator {
  EQUAL,
  NOT_EQUAL,
  LESS,
  LESS_OR_EQUAL,
  GREATER,
  GREATER_OR_EQUAL,
}

object CsParser : Grammar<CsScriptNode>() {
  val comment by regexToken("""//.*""", ignore = true)

  val lpar by literalToken("(")
  val rpar by literalToken(")")
  val lsqr by literalToken("[")
  val rsqr by literalToken("]")
  val lcurl by literalToken("{")
  val rcurl by literalToken("}")

  val semi by literalToken(";")
  val colon by literalToken(":")
  val ellipsis by literalToken("...")
  val dot by literalToken(".")
  val comma by literalToken(",")

  val `if` by literalToken("if")
  val `else` by literalToken("else")
  val `init` by literalToken("init")
  val `finally` by literalToken("finally")
  val switch by literalToken("switch")
  val case by literalToken("case")
  val default by literalToken("default")

  val plus by literalToken("+")
  val minus by literalToken("-")
  val div by literalToken("/")
  val times by literalToken("*")
  val mod by literalToken("%")
  val or by literalToken("||")
  val and by literalToken("&&")
  val eq by literalToken("==")
  val neq by literalToken("!=")
  val leq by literalToken("<=")
  val geq by literalToken(">=")
  val gt by literalToken(">")
  val lt by literalToken("<")

  val assign by literalToken("=")

  val not by literalToken("!")
  val pos by literalToken("+")
  val neg by literalToken("-")

  val num by regexToken("""[0-9]+(\.[0-9]+)?""")
  val ident by regexToken("""([^\W0-9]\w*)|(`[^`]+`)""")
  val str by regexToken(""" "[^"\v]*" """.trim { it == ' ' }) // extra spaces for readability
  val nl by regexToken("""\r?\n""", ignore = true)
  val ws by regexToken("""[^\S\r\n]+""", ignore = true)

  val numericalInfixOperators =
      mapOf(
          plus to NumericalInfixOperator.PLUS,
          minus to NumericalInfixOperator.MINUS,
          div to NumericalInfixOperator.DIV,
          times to NumericalInfixOperator.TIMES,
          mod to NumericalInfixOperator.MOD,
      )

  val comparisonOperators =
      mapOf(
          eq to ComparisonOperator.EQUAL,
          neq to ComparisonOperator.NOT_EQUAL,
          lt to ComparisonOperator.LESS,
          leq to ComparisonOperator.LESS_OR_EQUAL,
          gt to ComparisonOperator.GREATER,
          geq to ComparisonOperator.GREATER_OR_EQUAL,
      )

  val identifier by
      ident.use { if (text[0] == '`') text.substring(1 until text.length - 1) else text }

  val numLiteral by num.use { CsLiteralNode(text.toDouble().asCsNumber()) }
  val strLiteral by str.use { CsLiteralNode(text.substring(1 until text.length - 1).asCsString()) }
  val identifierExpression by identifier.map { CsIdentifierNode(it) }

  val expressionList by separatedTerms(parser { expression }, comma)

  val atomicExpression by
      numLiteral or strLiteral or identifierExpression or (-lpar * parser { expression } * -rpar)

  val attributeAccess by
      (-dot * identifier).map { value ->
        { lhs: CsExpressionNode -> CsAttributeAccessNode(lhs, value) }
      }
  val callArguments by
      (-lpar * optional(expressionList * optional(ellipsis) * -optional(comma)) * -rpar).map { value
        ->
        { lhs: CsExpressionNode -> CsCallNode(lhs, value?.t1 ?: emptyList(), value?.t2 != null) }
      }

  val primaryExpression: Parser<CsExpressionNode> by
      (atomicExpression * zeroOrMore(attributeAccess or callArguments)).map { (lhs, ops) ->
        ops.fold(lhs) { a, v -> v(a) }
      }

  val unaryExpression by
      (-plus * primaryExpression).map { CsPosOperatorNode(it) } or
          (-minus * primaryExpression).map { CsNegOperatorNode(it) } or
          (-not * primaryExpression).map { CsNotOperatorNode(it) } or
          primaryExpression

  val multiplicationOperator by times or div or mod
  val multiplication by
      leftAssociative(unaryExpression, multiplicationOperator) { l, o, r ->
        CsNumericalInfixOperatorNode(l, r, numericalInfixOperators[o.type]!!)
      }

  val additionOperator by plus or minus
  val addition by
      leftAssociative(multiplication, additionOperator) { l, o, r ->
        CsNumericalInfixOperatorNode(l, r, numericalInfixOperators[o.type]!!)
      }

  val comparisonOperator by eq or neq or lt or leq or gt or geq
  val comparison by
      leftAssociative(addition, comparisonOperator) { l, o, r ->
        CsComparisonOperatorNode(l, r, comparisonOperators[o.type]!!)
      }

  private val andChain by
      leftAssociative(comparison, and) { l, _, r ->
        CsBooleanInfixOperatorNode(l, r, BooleanInfixOperator.AND)
      }
  private val orChain by
      leftAssociative(andChain, or) { l, _, r ->
        CsBooleanInfixOperatorNode(l, r, BooleanInfixOperator.OR)
      }

  val expression: Parser<CsExpressionNode> by orChain

  val statementList by
      (zeroOrMore(optional(parser { statement }) * -semi) * optional(parser { statement })).map {
          (a, b) ->
        (a + b).filterNotNull()
      }
  val block by (-lcurl * statementList * -rcurl)

  val ifStatement: Parser<CsIfNode> by
      (-`if` * expression * block * optional(parser { elseBlock })).map { (expr, tbranch, fbranch)
        ->
        CsIfNode(expr, CsBlockNode(tbranch), fbranch?.let { CsBlockNode(it) })
      }
  val elseBlock: Parser<List<CsStatementNode>> =
      (-`else` * (block or ifStatement.map { listOf(it) }))

  val expressionCaseClause by
      (-case * expressionList * -colon * statementList).map { (expressions, body) ->
        CsExpressionCase(expressions, CsBlockNode(body))
      }
  val defaultCaseClause by
      (-default * -colon * statementList).map { body -> CsDefaultCase(CsBlockNode(body)) }
  val caseClause by expressionCaseClause or defaultCaseClause

  val switchStatement by
      (-switch * optional(expression) * -lcurl * zeroOrMore(caseClause) * -rcurl).map {
          (expression, cases) ->
        CsSwitchClause(expression, cases)
      }

  val assignmentStatement by
      (identifierExpression * -assign * expression).map { (i, v) -> CsAssignmentNode(i.name, v) }

  val statement: Parser<CsStatementNode> by
      (ifStatement or switchStatement or assignmentStatement or expression).let { parser ->
        object : Parser<CsStatementNode> {
          override fun tryParse(
              tokens: TokenMatchesSequence,
              fromPosition: Int,
          ): ParseResult<CsStatementNode> {
            val result = parser.tryParse(tokens, fromPosition)
            return if (result is Parsed) {
              var startToken: TokenMatch? = null
              var pos = fromPosition
              while (true) {
                val nextToken = tokens.get(pos++) ?: break
                if (!nextToken.type.ignored) {
                  startToken = nextToken
                  break
                }
              }
              object : Parsed<CsStatementNode>() {
                override val value = CsStatementContainer(result.value, startToken)
                override val nextPosition = result.nextPosition
              }
            } else {
              result
            }
          }
        }
      }

  val initBlock by -`init` * -lcurl * statementList * -rcurl
  val finallyBlock by -`finally` * -lcurl * statementList * -rcurl

  override val rootParser by
      (optional(initBlock) * -zeroOrMore(semi) * optional(finallyBlock) * statementList).map {
          (initialize, finalize, body) ->
        CsScriptNode(
            initialize?.let { CsBlockNode(it) },
            finalize?.let { CsBlockNode(it) },
            CsBlockNode(body))
      }

  private class CsTokenProducer(private val input: CharSequence) : TokenProducer {
    private val inputLength = input.length
    private var tokenIndex = 0
    private var pos = 0
    private var row = 1
    private var col = 1

    private var errorState = false

    private var lastToken: Token? = null
    private var deferredMatch: TokenMatch? = null

    private val autoSemiTokens = setOf(ident, num, str, rpar, rsqr, rcurl)

    override fun nextToken(): TokenMatch? {
      if (deferredMatch != null) {
        lastToken = deferredMatch!!.type
        return deferredMatch.also { deferredMatch = null }
      }

      if (pos > input.lastIndex || errorState) {
        return null
      }

      @Suppress("ReplaceManualRangeWithIndicesCalls")
      for (index in 0 until tokens.size) {
        val token = tokens[index]
        val matchLength = token.match(input, pos)
        if (matchLength == 0) continue

        val autoSemi =
            if (token == nl && lastToken in autoSemiTokens) {
              TokenMatch(semi, tokenIndex++, input, pos, matchLength, row, col)
            } else {
              null
            }
        val result = TokenMatch(token, tokenIndex++, input, pos, matchLength, row, col)

        for (i in pos until pos + matchLength) {
          if (input[i] == '\n') {
            row++
            col = 1
          } else {
            col++
          }
        }

        pos += matchLength

        if (token != ws && token != comment) {
          lastToken = token
        }
        autoSemi?.let {
          deferredMatch = result
          return it
        }
        return result
      }

      errorState = true
      return TokenMatch(noneMatched, tokenIndex++, input, pos, inputLength - pos, row, col)
    }
  }

  override val tokenizer by lazy {
    object : Tokenizer {
      override val tokens = this@CsParser.tokens

      override fun tokenize(input: String): TokenMatchesSequence {
        return TokenMatchesSequence(CsTokenProducer(input))
      }
    }
  }
}
