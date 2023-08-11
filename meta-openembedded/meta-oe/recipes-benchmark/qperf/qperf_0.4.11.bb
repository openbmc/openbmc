SUMMARY = "Measure socket and RDMA performance"
DESCRIPTION = "qperf measures bandwidth and latency between two nodes."
HOMEPAGE = "https://github.com/linux-rdma/qperf"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

inherit autotools-brokensep

SRCREV = "c706363815a38ff2c5cbc07b73e2cfaaa59bae0f"
SRC_URI = "git://github.com/linux-rdma/qperf.git;protocol=https;branch=master"

S = "${WORKDIR}/git"

do_configure() {
  ./cleanup
  ./autogen.sh
  oe_runconf
}
