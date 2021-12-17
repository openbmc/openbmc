FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
KERNEL_VERSION_SANITY_SKIP="1"
SRC_URI += "\
	file://lannister.cfg \
	"
