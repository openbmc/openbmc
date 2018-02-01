require ${BPN}.inc

PR = "r1"

SRC_URI[md5sum] = "40b479444bb06147429a276127981890"
SRC_URI[sha256sum] = "78e5ca334ee25185748660b4e612f984f4d3bced018f062278701429868f117b"

PNBLACKLIST[python-edbus] ?= "Fails to build with RSS http://errors.yoctoproject.org/Errors/Details/130600/ - the recipe will be removed on 2017-09-01 unless the issue is fixed"
