# Helper functions for checking feature variables.

inherit utils


def df_enabled(feature, value, d):
    return base_contains("DISTRO_FEATURES", feature, value, "", d)


def mf_enabled(feature, value, d):
    return base_contains("MACHINE_FEATURES", feature, value, "", d)


def cf_enabled(feature, value, d):
    return value if df_enabled(feature, value, d) \
        and mf_enabled(feature, value, d) \
            else ""
