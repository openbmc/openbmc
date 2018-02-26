# Skip QA check for library symbolic links (core issue is a packaging problem within 
# Singularity build / config: read up on the dev-so test for more info)
INSANE_SKIP_${PN} += "dev-so"

RDEPENDS_${PN} += "glibc python3 ca-certificates openssl bash e2fsprogs-mke2fs"
# Singularity expects to find python3 under the name python, therefore both
# cannot be installed at the same time.
RCONFLICTS_${PN} = "python"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYRIGHT.md;md5=be78c34e483dd7d8439358b1e024b294 \
                    file://LICENSE-LBNL.md;md5=45a007b527e1a9507aa7fa869f8d7ede \
                    file://LICENSE.md;md5=df4326b473db6424033f1d98a5645e30 \
                    file://debian/copyright;md5=ed267cf386d9b75ab1f27f407e935b10"

SRC_URI = "git://github.com/singularityware/singularity.git;protocol=https"
PV = "2.3.1+git${SRCPV}"
SRCREV = "e214d4ebf0a1274b1c63b095fd55ae61c7e92947"

S = "${WORKDIR}/git"

inherit pythonnative autotools-brokensep
EXTRA_OECONF = "--prefix=/usr/local"

pkg_postinst_${PN}() {
    # Singularity requires "python" to resolve to "python3" within the commandline.
    # This creates a symbolic link from python3 to python. A side-effect of this is
    # that scripts which expect Python 2 may fail to run correctly.
    ln -sr $D${bindir}/python3 $D${bindir}/python 

    # python3 expects CA certificates to be installed in a different place to where
    # they are actually installed. These lines link the two locations.
    rm -r $D${libdir}/ssl-1.1/certs
    ln -sr $D${sysconfdir}/ssl/certs $D${libdir}/ssl-1.1 
}
