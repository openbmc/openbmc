# Unfortunately various tunefiles don't include each other, so create
# a list of things to require based on the DEFAULTTUNE setting.
TUNEFILE[cortexr5] = "conf/machine/include/tune-cortexrm.inc"
TUNEFILE[cortexr5f] = "conf/machine/include/tune-cortexrm.inc"
TUNEFILE[cortexa9thf-neon] = "conf/machine/include/tune-cortexa9.inc"
TUNEFILE[cortexa53] = "conf/machine/include/tune-cortexa53.inc"
TUNEFILE[cortexa72] = "conf/machine/include/tune-cortexa72.inc"
TUNEFILE[cortexa72-cortexa53] = "conf/machine/include/tune-cortexa72-cortexa53.inc"
TUNEFILE[microblaze] = "conf/machine/include/tune-microblaze.inc"

# Default to arch-armv8a.inc
TUNEFILE = "${@ d.getVarFlag('TUNEFILE', d.getVar('DEFAULTTUNE')) if d.getVarFlag('TUNEFILE', d.getVar('DEFAULTTUNE')) else 'conf/machine/include/arm/arch-armv8a.inc'}"

require ${TUNEFILE}
