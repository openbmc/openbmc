SUMMARY = "GNU tool that controls the build process"
DESCRIPTION = "Make is a tool which controls the generation of executables and other non-source files of a \
program from the program's source files.  Make gets its knowledge of how to build your program from a file \
called the makefile, which lists each of the non-source files and how to compute it from other files."
HOMEPAGE = "http://www.gnu.org/software/make/"
SECTION = "devel"

SRC_URI = "${GNU_MIRROR}/make/make-${PV}.tar.bz2"

inherit autotools gettext pkgconfig texinfo

PROVIDES = "virtual/make"
