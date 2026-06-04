SUMMARY = "Google Authenticator PAM module"
HOMEPAGE = "https://github.com/google/google-authenticator-libpam"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
LICENSE = "Apache-2.0"

SRC_URI = "git://github.com/google/google-authenticator-libpam.git;branch=master;protocol=https"
SRCREV = "016774b0b75ee8f5e80028998d386de762ba1697"

DEPENDS = "libpam"

inherit autotools features_check

REQUIRED_DISTRO_FEATURES = "pam"

# Use the same dir location as PAM
EXTRA_OECONF = "--libdir=${base_libdir}" 

PACKAGES += "pam-google-authenticator"
FILES:pam-google-authenticator = "${base_libdir}/security/pam_google_authenticator.so"

RDEPENDS:pam-google-authenticator = "libpam"
