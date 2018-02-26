SUMMARY = "Portable Archive eXchange"
DESCRIPTION = "pax (Portable Archive eXchange) is the POSIX standard archive tool"
HOMEPAGE = "http://cvsweb.openbsd.org/cgi-bin/cvsweb/src/bin/pax/"
BUGTRACKER = "http://www.openbsd.org/query-pr.html"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=4b0b674dfdc56daa3832d4069b820ea0 \
                    file://src/pax.h;endline=40;md5=309d3e241c1d82069228e5a51e9b8d60 \
                    file://src/cpio.h;endline=40;md5=c3b4bbff6133a83387968617bbae8ac4 \
                    file://lib/vis.h;endline=40;md5=b283f759abd4a5ad7e014b80f51fc053"

SECTION = "base"
PR = "r2"

DEPENDS_append_libc-musl = " fts "

SRC_URI = "http://pkgs.fedoraproject.org/repo/pkgs/${BPN}/${BP}.tar.bz2/fbd9023b590b45ac3ade95870702a0d6/${BP}.tar.bz2 \
           file://fix_for_compile_with_gcc-4.6.0.patch \
           file://pax-3.4_fix_for_x32.patch \
           file://0001-include-sys-sysmacros.h-for-major-minor-definitions.patch \
           file://0001-Add-a-comment-for-fallthrough.patch \
           "

SRC_URI_append_libc-musl = " file://0001-Fix-build-with-musl.patch \
                             file://0001-use-strtoll-instead-of-strtoq.patch \
                           "

SRC_URI[md5sum] = "fbd9023b590b45ac3ade95870702a0d6"
SRC_URI[sha256sum] = "ac3c06048e02828077cf7757d3d142241429238893b91d529af29a2e8cc5623b"

inherit autotools
