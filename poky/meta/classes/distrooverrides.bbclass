# Turns certain DISTRO_FEATURES into overrides with the same
# name plus a df- prefix. Ensures that these special
# distro features remain set also for native and nativesdk
# recipes, so that these overrides can also be used there.
#
# This makes it simpler to write .bbappends that only change the
# task signatures of the recipe if the change is really enabled,
# for example with:
#   do_install:append:df-my-feature () { ... }
# where "my-feature" is a DISTRO_FEATURE.
#
# The class is meant to be used in a layer.conf or distro
# .inc file with:
# INHERIT += "distrooverrides"
# DISTRO_FEATURES_OVERRIDES += "my-feature"
#
# Beware that this part of OVERRIDES changes during parsing, so usage
# of these overrides should be limited to .bb and .bbappend files,
# because then DISTRO_FEATURES is final.

DISTRO_FEATURES_OVERRIDES ?= ""
DISTRO_FEATURES_OVERRIDES[doc] = "A space-separated list of <feature> entries. \
Each entry is added to OVERRIDES as df-<feature> if <feature> is in DISTRO_FEATURES."

DISTRO_FEATURES_FILTER_NATIVE:append = " ${DISTRO_FEATURES_OVERRIDES}"
DISTRO_FEATURES_FILTER_NATIVESDK:append = " ${DISTRO_FEATURES_OVERRIDES}"

# If DISTRO_FEATURES_OVERRIDES or DISTRO_FEATURES show up in a task
# signature because of this line, then the task dependency on
# OVERRIDES itself should be fixed. Excluding these two variables
# with DISTROOVERRIDES[vardepsexclude] would just work around the problem.
DISTROOVERRIDES .= "${@ ''.join([':df-' + x for x in sorted(set(d.getVar('DISTRO_FEATURES_OVERRIDES').split()) & set((d.getVar('DISTRO_FEATURES') or '').split()))]) }"
