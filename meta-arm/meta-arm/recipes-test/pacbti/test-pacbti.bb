SUMMARY = "Test to verify that PAC/BTI is enabled"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://pacbti.c;beginline=2;endline=2;md5=6ec41034e04432ee375d0e14fba596f4"

SRC_URI = "file://pacbti.c"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_compile() {
    # Compile with -zforce-bti with fatal warnings, so the link fails if PAC/BTI
    # is requested but gcc/glibc are built without it.
    ${CC} ${CFLAGS} ${LDFLAGS} -z force-bti -Werror -Wl,--fatal-warnings ${S}/pacbti.c

    # If we have a binary, check that the AArch64 feature list in the binary
    # actually enables PAC/BTI.
    ${READELF} --notes a.out | grep "AArch64 feature" >notes
    grep BTI notes
    grep PAC notes
}

COMPATIBLE_HOST = "aarch64.*-linux"
