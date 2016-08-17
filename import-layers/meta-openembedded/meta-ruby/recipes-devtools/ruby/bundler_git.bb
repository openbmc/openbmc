SUMMARY = "Makes sure Ruby applications run the same code on every machine."
DESCRIPTION = "Bundler makes sure Ruby applications run the same code \
on every machine. It does this by managing the gems that the \
application depends on. Given a list of gems, it can automatically \
download and install those gems, as well as any other gems needed by \
the gems that are listed. Before installing gems, it checks the \
versions of every gem to make sure that they are compatible, and can \
all be loaded at the same time. After the gems have been installed, \
Bundler can help you update some or all of them when new versions \
become available. Finally, it records the exact versions that have \
been installed, so that others can install the exact same gems."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=196bb963e601609817d7e9ac9a64a867"

SRCREV = "06dc8472b9142fd6aaefff780d6d252c20dc2a04"

BRANCH = "1-9-stable"
PV = "1.9.4"

S = "${WORKDIR}/git"

SRC_URI = " \
    git://github.com/bundler/bundler.git;branch=${BRANCH} \
    "

inherit ruby

RDEPENDS_${PN} = "git"

BBCLASSEXTEND = "native"
