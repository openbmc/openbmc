require  meta-perl-base.bb

SUMMARY = "meta-perl build ptest image"

inherit features_check

REQUIRED_DISTRO_FEATURES += "ptest"

IMAGE_INSTALL += "packagegroup-meta-perl-ptest"
