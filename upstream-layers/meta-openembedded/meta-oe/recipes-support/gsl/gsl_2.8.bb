include gsl.inc

SRC_URI = "${GNU_MIRROR}/gsl/gsl-${PV}.tar.gz"
SRC_URI[sha256sum] = "6a99eeed15632c6354895b1dd542ed5a855c0f15d9ad1326c6fe2b2c9e423190"

# GNU_MIRROR resolves to ftpmirror.gnu.org, which round-robins to mirrors whose
# directory listings are not always parseable; check the canonical archive.
UPSTREAM_CHECK_URI = "https://ftp.gnu.org/gnu/gsl/"
UPSTREAM_CHECK_REGEX = "gsl-(?P<pver>\d+(\.\d+)+)\.tar\.gz"
