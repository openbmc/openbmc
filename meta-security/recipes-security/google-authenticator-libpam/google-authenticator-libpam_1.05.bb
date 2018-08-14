SUMMARY = "Google Authenticator PAM module"
HOME_PAGE = "https://github.com/google/google-authenticator-libpam"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
LICENSE = "Apache-2.0"

SRC_URI = "git://github.com/google/google-authenticator-libpam.git"
SRCREV = "7365ed10d54393fb4c100cac063ae8edb744eac6"

DEPENDS = "libpam"

S = "${WORKDIR}/git"

inherit autotools distro_features_check

REQUIRED_DISTRO_FEATURES = "pam"

PACKAGES += "pam-google-authenticator"
FILES_pam-google-authenticator = "${libdir}/security/pam_google_authenticator.so"

RDEPNEDS_pam-google-authenticator  = "libpam"
