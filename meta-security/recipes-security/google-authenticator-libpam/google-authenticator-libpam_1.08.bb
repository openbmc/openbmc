SUMMARY = "Google Authenticator PAM module"
HOME_PAGE = "https://github.com/google/google-authenticator-libpam"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
LICENSE = "Apache-2.0"

SRC_URI = "git://github.com/google/google-authenticator-libpam.git"
SRCREV = "2c7415d950fb0b4a7f779f045910666447b100ef"

DEPENDS = "libpam"

S = "${WORKDIR}/git"

inherit autotools features_check

REQUIRED_DISTRO_FEATURES = "pam"

# Use the same dir location as PAM
EXTRA_OECONF = "--libdir=${base_libdir}" 

PACKAGES += "pam-google-authenticator"
FILES_pam-google-authenticator = "${base_libdir}/security/pam_google_authenticator.so"

RDEPNEDS_pam-google-authenticator  = "libpam"
