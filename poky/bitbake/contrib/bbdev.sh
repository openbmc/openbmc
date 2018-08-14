# This is a shell function to be sourced into your shell or placed in your .profile,
# which makes setting things up for BitBake a bit easier.
#
# The author disclaims copyright to the contents of this file and places it in the
# public domain.

bbdev () {
	local BBDIR PKGDIR BUILDDIR
	if test x"$1" = "x--help"; then echo >&2 "syntax: bbdev [bbdir [pkgdir [builddir]]]"; return 1; fi
	if test x"$1" = x; then BBDIR=`pwd`; else BBDIR=$1; fi
	if test x"$2" = x; then PKGDIR=`pwd`; else PKGDIR=$2; fi
	if test x"$3" = x; then BUILDDIR=`pwd`; else BUILDDIR=$3; fi

	BBDIR=`readlink -f $BBDIR`
	PKGDIR=`readlink -f $PKGDIR`
	BUILDDIR=`readlink -f $BUILDDIR`
	if ! (test -d $BBDIR && test -d $PKGDIR && test -d $BUILDDIR); then
		echo >&2 "syntax: bbdev [bbdir [pkgdir [builddir]]]"
		return 1
	fi
	
	PATH=$BBDIR/bin:$PATH
	BBPATH=$BBDIR
	if test x"$BBDIR" != x"$PKGDIR"; then
		BBPATH=$PKGDIR:$BBPATH
	fi
	if test x"$PKGDIR" != x"$BUILDDIR"; then
		BBPATH=$BUILDDIR:$BBPATH
	fi
	export BBPATH
}
