package xyz.qwewqa.relive.simulator.core.presets.memoir

val memoirs = listOf(
    UnshakableFeelings,
    FriendsAtTheAquarium,
    UrashimaTaroPerformanceFlyer,
    CoStarringWithHatsuneMiku,
    FirstAnnivSeishoMusicAcademy,
    FirstAnnivSiegfeldInstituteOfMusic,
    FirstAnnivRinmeikanGirlsSchool,
    FirstAnnivFrontierSchoolOfArts,
    BandsmansGreeting,
    CrazyMadScientist,
    ThePhantomAndChristine,
    PoolsideIncident,
    MerryChristmas2019,
    PrinceAndPrincessEtude,
    SunsetLabMemBadge,
    ReminiscenceMelody,
    KappoTomoyesPosterGirl,
    UnburnedFlowerUnwitheredFlame,
    XIIHangedManReverse,
    VILoversReverse,
    XVITowerUpright,
    TheGreatYearEndCleanup,
    ConfidantsOnADate,
    SparklingStageChika
).sortedBy { it.name }.sortedBy { it.cutinData.any { cutin -> cutin == null } }.associateBy { it.name }
