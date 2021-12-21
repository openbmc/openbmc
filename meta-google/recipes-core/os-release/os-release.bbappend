python() {
    # Instead of using BB_ENV_EXTRAWHITE, we can get info from the
    # shell environment this way.
    origenv = d.getVar("BB_ORIGENV", False)
    memo = origenv.getVar("BUILD_MEMO", False)
    if memo:
        d.setVar("BUILD_MEMO", memo)
}

IMAGE_TYPE = "${GBMC_CONFIG}"
OS_RELEASE_FIELDS:append:gbmc = " BUILD_MEMO IMAGE_TYPE"
