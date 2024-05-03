FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# We want to remove the python support from the target package so that we do
# not end up with a python dependency on the flash image.
PACKAGECONFIG:remove:class-target = "python"

# There is an issue with the upstream `inherit_defer` ordering where a
# `class-target` gets applied to the inherit portion, even for a native target.
# See https://lore.kernel.org/openembedded-core/1bb12e6054301a5d3390991e1d1f0e013036a816.camel@linuxfoundation.org/
PACKAGECONFIG:remove:class-native = "python"
