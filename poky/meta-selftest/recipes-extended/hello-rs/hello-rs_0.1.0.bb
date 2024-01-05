SUMMARY = "Simple hello world example"
HOMEPAGE = "https://github.com/akiernan/hello-bin"
LICENSE = "Unlicense"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7246f848faa4e9c9fc0ea91122d6e680"

SRC_URI = "git://github.com/akiernan/hello-bin.git;protocol=https;branch=main;subpath=rust \
           file://0001-Greet-OE-Core.patch \
           git://github.com/akiernan/hello-lib.git;protocol=https;branch=main;name=hello-lib;destsuffix=hello-lib;type=git-dependency \
	   "
SRCREV = "d3d096eda182644868f8e7458dcfa538ff637db3"

SRCREV_FORMAT .= "_hello-lib"
SRCREV_hello-lib = "59c84574e844617043cf337bc8fa537cf87ad8ae"

S = "${WORKDIR}/rust"

inherit cargo cargo-update-recipe-crates ptest-cargo

# Remove this when the recipe is reproducible
EXCLUDE_FROM_WORLD = "1"

require ${BPN}-crates.inc
