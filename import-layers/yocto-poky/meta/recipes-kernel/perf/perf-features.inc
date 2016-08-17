PERF_FEATURES_ENABLE ?= "perf-scripting perf-tui"

def perf_feature_enabled(feature, trueval, falseval, d):
    """
    Check which perf features are enabled.

    The PERF_FEATURES_ENABLE variable lists the perf features to
    enable.  Override it if you want something different from what's
    listed above, which is the default.  If empty, the build won't
    enable any features (which may be exactly what you want, just a
    barebones perf without any extra baggage, what you get if you
    specify an empty feature list).

    Available perf features:
      perf-scripting: enable support for Perl and Python bindings
      perf-tui: enable support for the perf TUI (via libnewt)

    """
    enabled_features = d.getVar("PERF_FEATURES_ENABLE", True) or ""
    if feature in enabled_features:
        return trueval
    return falseval
