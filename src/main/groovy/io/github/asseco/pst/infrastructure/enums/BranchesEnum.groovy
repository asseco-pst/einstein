package io.github.asseco.pst.infrastructure.enums

enum BranchesEnum {
    main,
    develop,
    next

    static boolean isValidBranch(String version) {
        return values().any { it.name().equalsIgnoreCase(version) }
    }
}