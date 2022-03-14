FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:p10bmc = " file://a3.json file://keys/"

OTPTOOL_CONFIG:p10bmc = "${WORKDIR}/a3.json"
OTPTOOL_KEY_DIR:p10bmc = "${WORKDIR}/keys/"

# !!! Do not copy p10bmc's use of little-endian key ordering !!!
#
# The prefered order for production silicon is big-endian. Little-endian is necessary for p10bmc
# platforms due to development history involving pre-production AST2600 silicon. More discussion
# here:
#
# https://gerrit.openbmc-project.xyz/c/openbmc/openbmc/+/50716
SOCSEC_SIGN_EXTRA_OPTS = "--rsa_key_order=little"

do_deploy:prepend:p10bmc() {
	# otptool needs access to the public and private socsec signing keys in the keys/ directory
	openssl rsa -in ${SOCSEC_SIGN_KEY} -pubout > ${WORKDIR}/keys/rsa_pub_oem_dss_key.pem
}
