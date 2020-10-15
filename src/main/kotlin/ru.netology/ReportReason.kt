package ru.netology

enum class ReportReason(val reasonCode: Int) {
    SPAM(0),
    CHILD_PORN(1),
    EXTREMISM(2),
    VIOLENCE(3),
    DRUGS_PROPAGANDA(4),
    ADULT_ONLY(5),
    ABUSE(6),
    APPEAL_FOR_SUICIDE(8)
}