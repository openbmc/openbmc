SUMARRY = "SCAP content for various platforms, OE changes"

SRCREV = "5fdfdcb2e95afbd86ace555beca5d20cbf1043ed"
SRC_URI = "git://github.com/akuster/scap-security-guide.git;branch=oe-0.1.44; \
           file://0001-Fix-XML-parsing-of-the-remediation-functions-file.patch \
           file://0002-Fixed-the-broken-fix-when-greedy-regex-ate-the-whole.patch \
          "
PV = "0.1.44+git${SRCPV}"

require scap-security-guide.inc

EXTRA_OECMAKE += "-DSSG_PRODUCT_OPENEMBEDDED=ON"
