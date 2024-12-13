SUMMARY = "Google Authenticator PAM module"
HOME_PAGE = "https://github.com/google/google-authenticator-libpam"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
LICENSE = "Apache-2.0"

SRC_URI = "git://github.com/google/google-authenticator-libpam.git;branch=master;protocol=https"
SRCREV = "962f353aac6cfc7b804547319db40f8b804f0b6c"

DEPENDS = "libpam"

S = "${UNPACKDIR}/git"

inherit autotools features_check

REQUIRED_DISTRO_FEATURES = "pam"

# Use the same dir location as PAM
EXTRA_OECONF = "--libdir=${base_libdir}" 

PACKAGES += "pam-google-authenticator"
FILES:pam-google-authenticator = "${base_libdir}/security/pam_google_authenticator.so"

RDEPNEDS_pam-google-authenticator  = "libpam"
