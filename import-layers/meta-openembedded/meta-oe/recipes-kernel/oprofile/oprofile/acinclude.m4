dnl AX_KERNEL_OPTION(option, action-if-found, action-if-not-found)
dnl see if autoconf.h defines the option
AC_DEFUN([AX_KERNEL_OPTION], [
SAVE_CFLAGS=$CFLAGS
CFLAGS="-I$KINC -O2 -D__KERNEL__"
AC_TRY_COMPILE( [#include <linux/config.h>],
[
#ifndef $1
break_me_hard(\\\);
#endif
],[$2],[$3],)
CFLAGS=$SAVE_CFLAGS
])

dnl Handle the 2.4 module inside module/
AC_DEFUN([AX_CONFIG_MODULE],
[
if test ! -f $KINC/linux/autoconf.h; then
	AC_MSG_ERROR([no suitably configured kernel include tree found])
fi

dnl  --- Get Linux kernel version and compile parameters ---

AC_SUBST(KVERS)
AC_MSG_CHECKING([for kernel version])
dnl it's like this to handle mandrake's fubar version.h - bug #471448
eval KVERS=`gcc -I$KINC -E -dM $KINC/linux/version.h | grep -w UTS_RELEASE | awk '{print $[]3}'`
AC_MSG_RESULT([$KVERS])
case "$KVERS" in
2.2.*|2.4.*) ;;
*) AC_MSG_ERROR([Unsupported kernel version])
esac

dnl Check for the minimal kernel version supported
AC_MSG_CHECKING([kernel version])
AX_KERNEL_VERSION(2, 2, 10, <=, AC_MSG_RESULT([ok]), AC_MSG_ERROR([check html documentation install section]))

dnl linux/spinlock.h added at some point in past
AC_MSG_CHECKING([for $KINC/linux/spinlock.h])
if test -f $KINC/linux/spinlock.h; then
	EXTRA_CFLAGS_MODULE="$EXTRA_CFLAGS_MODULE -DHAVE_LINUX_SPINLOCK_HEADER"
	AC_MSG_RESULT([yes])
else
	AC_MSG_RESULT([no])
fi

AC_MSG_CHECKING([for rtc_lock])
gcc -I$KINC -E $KINC/linux/mc146818rtc.h | grep rtc_lock >/dev/null
if test "$?" -eq 0; then
	EXTRA_CFLAGS_MODULE="$EXTRA_CFLAGS_MODULE -DRTC_LOCK"
	AC_MSG_RESULT([yes])
else
	AC_MSG_RESULT([no])
fi
	 
arch="unknown"
AC_MSG_CHECKING(for x86-64 architecture)
AX_KERNEL_OPTION(CONFIG_X86_64, x8664=1, x8664=0)
AX_MSG_RESULT_YN($x8664)
BUILD_HAMMER=no
if test "$x8664" -eq 1; then
	arch="x86"
	BUILD_HAMMER=yes
else
	AC_MSG_CHECKING(for x86 architecture)
	AX_KERNEL_OPTION(CONFIG_X86, x86=1, x86=0)
	AX_KERNEL_OPTION(CONFIG_X86_WP_WORKS_OK, x86=1, x86=$x86)
	AX_MSG_RESULT_YN($x86)
	test "$x86" = 1 && arch="x86"
	
	if test "$arch" = "unknown"; then
  		AC_MSG_CHECKING(for ia64 architecture)
  		AX_KERNEL_OPTION(CONFIG_IA64, ia64=1, ia64=0)
  		AX_MSG_RESULT_YN($ia64)
  		test "$ia64" = 1 && arch="ia64"
	fi

fi
AC_SUBST(BUILD_HAMMER)

test "$arch" = "unknown" && AC_MSG_ERROR(Unsupported architecture)

dnl check to see if kernel verion appropriate for arch
AC_MSG_CHECKING(arch/kernel version combination)
case "$arch" in
ia64)
	AX_KERNEL_VERSION(2, 4, 18, <, AC_MSG_RESULT([ok]),
		AC_MSG_ERROR([unsupported arch/kernel])) ;;
*) AC_MSG_RESULT([ok])
esac

dnl for now we do not support PREEMPT patch
AC_MSG_CHECKING([for preempt patch])
AX_KERNEL_OPTION(CONFIG_PREEMPT,preempt=1,preempt=0)
AX_MSG_RESULT_YN([$preempt])
test "$preempt" = 0 || AC_MSG_ERROR([unsupported kernel configuration : CONFIG_PREEMPT])

AC_SUBST(KINC)

MODINSTALLDIR=/lib/modules/$KVERS
 
OPROFILE_MODULE_ARCH=$arch
AC_SUBST(OPROFILE_MODULE_ARCH)
]
)

dnl AX_MSG_RESULT_YN(a)
dnl results "yes" iff a==1, "no" else
AC_DEFUN([AX_MSG_RESULT_YN], [x=no
test "x$1" = "x1" && x=yes
AC_MSG_RESULT($x)])

dnl AX_MALLOC_ATTRIBUTE - see if gcc will take __attribute__((malloc))
AC_DEFUN([AX_MALLOC_ATTRIBUTE],
[
AC_MSG_CHECKING([whether malloc attribute is understood])
SAVE_CFLAGS=$CFLAGS
CFLAGS="-Werror $CFLAGS"
AC_TRY_COMPILE(,[
void monkey() __attribute__((malloc));
],AC_MSG_RESULT([yes]); AC_DEFINE(MALLOC_ATTRIBUTE_OK, 1, [whether malloc attribute is understood]), AC_MSG_RESULT([no]))
CFLAGS=$SAVE_CFLAGS 
]
)

dnl builtin_expect is used in module we can't add that in config.h
AC_DEFUN([AX_BUILTIN_EXPECT],
[
AC_MSG_CHECKING([whether __builtin_expect is understood])
SAVE_CFLAGS=$CFLAGS
CFLAGS="-Werror $CFLAGS"
AC_TRY_LINK(,[
int i;
if (__builtin_expect(i, 0)) { }
],
AC_MSG_RESULT([yes]); EXTRA_CFLAGS_MODULE="$EXTRA_CFLAGS_MODULE -DEXPECT_OK",
AC_MSG_RESULT([no]);)
CFLAGS=$SAVE_CFLAGS 
]
) 

dnl AX_EXTRA_DIRS - Let user specify extra dirs for include/libs
AC_DEFUN([AX_EXTRA_DIRS],
[
AC_ARG_WITH(extra-includes,
[  --with-extra-includes=DIR    add extra include paths],
  use_extra_includes="$withval",
  use_extra_includes=NO
)
if test -n "$use_extra_includes" && \
        test "$use_extra_includes" != "NO"; then
  ac_save_ifs=$IFS
  IFS=':'
  for dir in $use_extra_includes; do
    extra_includes="$extra_includes -I$dir"
  done
  IFS=$ac_save_ifs
  CPPFLAGS="$CPPFLAGS $extra_includes"
fi

AC_ARG_WITH(extra-libs,
[  --with-extra-libs=DIR        add extra library paths],
  use_extra_libs=$withval,
  use_extra_libs=NO
)
if test -n "$use_extra_libs" && \
        test "$use_extra_libs" != "NO"; then
   ac_save_ifs=$IFS
   IFS=':'
   for dir in $use_extra_libs; do
     extra_libraries="$extra_libraries -L$dir"
   done
   IFS=$ac_save_ifs
   LDFLAGS="$LDFLAGS $extra_libraries"
fi
]
)

dnl AX_POPT_CONST - check popt prototype
AC_DEFUN([AX_POPT_CONST],
[
AC_MSG_CHECKING([popt prototype])
SAVE_CXXFLAGS=$CXXFLAGS
CXXFLAGS="-Werror $CXXFLAGS"
AC_TRY_COMPILE([#include <popt.h>],
[
int c; char **v;
poptGetContext(0, c, v, 0, 0);
],
AC_MSG_RESULT([takes char **]);,
AC_MSG_RESULT([takes const char **]); AC_DEFINE(CONST_POPT, 1, [whether popt prototype takes a const char **]))
CXXFLAGS="$SAVE_CXXFLAGS"
]
)

dnl AX_CHECK_SSTREAM - check if local sstream is needed to compile OK
AC_DEFUN([AX_CHECK_SSTREAM],
[
AC_MSG_CHECKING([whether to use included sstream])
AC_TRY_COMPILE([#include <sstream>], [], 
AC_MSG_RESULT([no]);,
AC_MSG_RESULT([yes]); OP_CXXFLAGS="$OP_CXXFLAGS -I\${top_srcdir}/include")
]
)

dnl AX_CHECK_TYPEDEF(typedef_name, type, action-if-true, action-if-false)
dnl exec action-if-true if typedef_name is a typedef to type else exec 
dnl action-if-false
dnl currently work only with type typedef'ed in stddef.h
AC_DEFUN([AX_CHECK_TYPEDEF], [
dnl AC_LANG_PUSH(C) not in autoconf 2.13
AC_LANG_SAVE
AC_LANG_C
SAVE_CFLAGS=$CFLAGS
CFLAGS="-Werror $CFLAGS"

AC_TRY_COMPILE(
  [
  #include <stddef.h>
  ],
  [
  typedef void (*fct1)($1);
  typedef void (*fct2)($2);
  fct1 f1 = 0;
  fct2 f2 = 0;
  if (f1 == f2) {}
  ],
[$3],[$4])

CFLAGS=$SAVE_CFLAGS
AC_LANG_RESTORE
])


dnl AX_TYPEDEFED_NAME(typedef_name, candidate_list, var_name)
dnl set var_name to the typedef name of $1 which must be in canditate_list
dnl else produce a fatal error
AC_DEFUN([AX_TYPEDEFED_NAME], [
	AC_MSG_CHECKING([type of $1])
	for f in $2; do
		AX_CHECK_TYPEDEF($1, $f, $3="$f", $3="")
		if test -n "${$3}"; then
			break
		fi
	done
	if test -n "${$3}"; then
		AC_MSG_RESULT([${$3}])
	else
		AC_MSG_ERROR([not found])
	fi
])

dnl find a binary in the path
AC_DEFUN([QT_FIND_PATH],
[
	AC_MSG_CHECKING([for $1])
	AC_CACHE_VAL(qt_cv_path_$1,
	[
		qt_cv_path_$1="NONE"
		if test -n "$$2"; then
			qt_cv_path_$1="$$2";
		else
			dirs="$3"
			qt_save_IFS=$IFS
			IFS=':'
			for dir in $PATH; do
				dirs="$dirs $dir"
			done
			IFS=$qt_save_IFS
 
			for dir in $dirs; do
				if test -x "$dir/$1"; then
					if test -n "$5"; then
						evalstr="$dir/$1 $5 2>&1 "
						if eval $evalstr; then
							qt_cv_path_$1="$dir/$1"
							break
						fi
					else
						qt_cv_path_$1="$dir/$1"
						break
					fi
				fi
			done
		fi
	])
 
	if test -z "$qt_cv_path_$1" || test "$qt_cv_path_$1" = "NONE"; then
		AC_MSG_RESULT(not found)
		$4
	else
		AC_MSG_RESULT($qt_cv_path_$1)
		$2=$qt_cv_path_$1
	fi
])

dnl Find the uic compiler on the path or in qt_cv_dir
AC_DEFUN([QT_FIND_UIC],
[
	QT_FIND_PATH(uic, ac_uic, $qt_cv_dir/bin)
	if test -z "$ac_uic" -a "$FATAL" = 1; then
		AC_MSG_ERROR([uic binary not found in \$PATH or $qt_cv_dir/bin !])
	fi
])
 
dnl Find the right moc in path/qt_cv_dir
AC_DEFUN([QT_FIND_MOC],
[
	QT_FIND_PATH(moc2, ac_moc2, $qt_cv_dir/bin)
	QT_FIND_PATH(moc, ac_moc1, $qt_cv_dir/bin)

	if test -n "$ac_moc1" -a -n "$ac_moc2"; then
		dnl found both. Prefer Qt3's if it exists else moc2
		$ac_moc1 -v 2>&1 | grep "Qt 3" >/dev/null
		if test "$?" = 0; then
			ac_moc=$ac_moc1;
		else
			ac_moc=$ac_moc2;
		fi
	else
		if test -n "$ac_moc1"; then
			ac_moc=$ac_moc1;
		else
			ac_moc=$ac_moc2;
		fi
	fi

	if test -z "$ac_moc"  -a "$FATAL" = 1; then
		AC_MSG_ERROR([moc binary not found in \$PATH or $qt_cv_dir/bin !])
	fi
])

dnl check a particular libname
AC_DEFUN([QT_TRY_LINK],
[
	SAVE_LIBS="$LIBS"
	LIBS="$LIBS $1"
	AC_TRY_LINK([
	#include <qglobal.h>
	#include <qstring.h>
		],
	[
	QString s("mangle_failure");
	#if (QT_VERSION < 221)
	break_me_(\\\);
	#endif
	],
	qt_cv_libname=$1,
	)
	LIBS="$SAVE_LIBS"
])
 
dnl check we can do a compile
AC_DEFUN([QT_CHECK_COMPILE],
[
	AC_MSG_CHECKING([for Qt library name])
 
	AC_CACHE_VAL(qt_cv_libname,
	[
		AC_LANG_CPLUSPLUS
		SAVE_CXXFLAGS=$CXXFLAGS
		CXXFLAGS="$CXXFLAGS $QT_INCLUDES $QT_LDFLAGS" 

		for libname in -lqt-mt -lqt3 -lqt2 -lqt;
		do
			QT_TRY_LINK($libname)
			if test -n "$qt_cv_libname"; then
				break;
			fi
		done

		CXXFLAGS=$SAVE_CXXFLAGS
	])

	if test -z "$qt_cv_libname"; then
		AC_MSG_RESULT([failed]) 
		if test "$FATAL" = 1 ; then
			AC_MSG_ERROR([Cannot compile a simple Qt executable. Check you have the right \$QTDIR !])
		fi
	else
		AC_MSG_RESULT([$qt_cv_libname])
	fi
])

dnl get Qt version we're using
AC_DEFUN([QT_GET_VERSION],
[
	AC_CACHE_CHECK([Qt version],lyx_cv_qtversion,
	[
		AC_LANG_CPLUSPLUS
		SAVE_CPPFLAGS=$CPPFLAGS
		CPPFLAGS="$CPPFLAGS $QT_INCLUDES"

		cat > conftest.$ac_ext <<EOF
#line __oline__ "configure"
#include "confdefs.h"
#include <qglobal.h>
"%%%"QT_VERSION_STR"%%%"
EOF
		lyx_cv_qtversion=`(eval "$ac_cpp conftest.$ac_ext") 2>&5 | \
			grep '^"%%%"'  2>/dev/null | \
			sed -e 's/"%%%"//g' -e 's/"//g'`
		rm -f conftest.$ac_ext
		CPPFLAGS=$SAVE_CPPFLAGS
	])
 
	QT_VERSION=$lyx_cv_qtversion
	AC_SUBST(QT_VERSION)
])
 
dnl start here 
AC_DEFUN([QT_DO_IT_ALL],
[
	dnl Please leave this alone. I use this file in
	dnl oprofile.
	FATAL=0

	AC_ARG_WITH(qt-dir, [  --with-qt-dir           where the root of Qt is installed ],
		[ qt_cv_dir=`eval echo "$withval"/` ])
	 
	AC_ARG_WITH(qt-includes, [  --with-qt-includes      where the Qt includes are. ],
		[ qt_cv_includes=`eval echo "$withval"` ])
 
	AC_ARG_WITH(qt-libraries, [  --with-qt-libraries     where the Qt library is installed.],
		[  qt_cv_libraries=`eval echo "$withval"` ])

	dnl pay attention to $QTDIR unless overridden
	if test -z "$qt_cv_dir"; then
		qt_cv_dir=$QTDIR
	fi
 
	dnl derive inc/lib if needed
	if test -n "$qt_cv_dir"; then
		if test -z "$qt_cv_includes"; then
			qt_cv_includes=$qt_cv_dir/include
		fi
		if test -z "$qt_cv_libraries"; then
			qt_cv_libraries=$qt_cv_dir/lib
		fi
	fi

	dnl flags for compilation
	QT_INCLUDES=
	QT_LDFLAGS=
	if test -n "$qt_cv_includes"; then
		QT_INCLUDES="-I$qt_cv_includes"
	fi
	if test -n "$qt_cv_libraries"; then
		QT_LDFLAGS="-L$qt_cv_libraries"
	fi
	AC_SUBST(QT_INCLUDES)
	AC_SUBST(QT_LDFLAGS)
 
	QT_FIND_MOC
	MOC=$ac_moc
	AC_SUBST(MOC)
	QT_FIND_UIC
	UIC=$ac_uic
	AC_SUBST(UIC)

	QT_CHECK_COMPILE
 
	QT_LIB=$qt_cv_libname;
	AC_SUBST(QT_LIB)

	if test -n "$qt_cv_libname"; then
		QT_GET_VERSION
	fi
])

dnl AX_CXXFLAGS_OPTIONS(var-name, option)
dnl add option to var-name if $CXX support it.
AC_DEFUN([AX_CHECK_PRECOMPILED_HEADER], [
AC_MSG_CHECKING([whether ${CXX} support precompiled header])
AC_LANG_SAVE
AC_LANG_CPLUSPLUS
SAVE_CXXFLAGS=$CXXFLAGS
dnl we consider than if -Winvalid-pch is accepted pch will works ...
CXXFLAGS=-Winvalid-pch
dnl but we don't want -Winvalid-pch else compilation will fail due -Werror and
dnl the fact than some pch will be invalid for the given compilation option
AC_TRY_COMPILE(,[;],AC_MSG_RESULT([yes]); $1="${$1} -include bits/stdc++.h", AC_MSG_RESULT([no]))
CXXFLAGS=$SAVE_CXXFLAGS
AC_LANG_RESTORE
])

dnl AX_CHECK_DOCBOOK
AC_DEFUN([AX_CHECK_DOCBOOK], [
# It's just rude to go over the net to build
XSLTPROC_FLAGS=--nonet
DOCBOOK_ROOT=
if test ! -f /etc/xml/catalog; then
	for i in /usr/share/sgml/docbook/stylesheet/xsl/nwalsh /usr/share/sgml/docbook/xsl-stylesheets/;
	do
		if test -d "$i"; then
			DOCBOOK_ROOT=$i
		fi
	done

	# Last resort - try net
	if test -z "$DOCBOOK_ROOT"; then
		XSLTPROC_FLAGS=
	fi
else
	XML_CATALOG=/etc/xml/catalog
	CAT_ENTRY_START='<!--'
	CAT_ENTRY_END='-->'
fi

AC_CHECK_PROG(XSLTPROC,xsltproc,xsltproc,)
XSLTPROC_WORKS=no
if test -n "$XSLTPROC"; then
	AC_MSG_CHECKING([whether xsltproc works])

	if test -n "$XML_CATALOG"; then
		DB_FILE="http://docbook.sourceforge.net/release/xsl/current/xhtml/docbook.xsl"
	else
		DB_FILE="$DOCBOOK_ROOT/docbook.xsl"
	fi

	$XSLTPROC $XSLTPROC_FLAGS $DB_FILE >/dev/null 2>&1 << END
<?xml version="1.0" encoding='ISO-8859-1'?>
<!DOCTYPE book PUBLIC "-//OASIS//DTD DocBook XML V4.1.2//EN" "http://www.oasis-open.org/docbook/xml/4.1.2/docbookx.dtd">
<book id="test">
</book>
END
	if test "$?" = 0; then
		XSLTPROC_WORKS=yes
	fi
	AC_MSG_RESULT($XSLTPROC_WORKS)
fi
AM_CONDITIONAL(have_xsltproc, test "$XSLTPROC_WORKS" = "yes")

AC_SUBST(XML_CATALOG)
AC_SUBST(XSLTPROC_FLAGS)
AC_SUBST(DOCBOOK_ROOT)
AC_SUBST(CAT_ENTRY_START)
AC_SUBST(CAT_ENTRY_END)
])

dnl AX_CFLAGS_OPTIONS(var-name, option)
dnl add option to var-name if $CC support it.
AC_DEFUN([AX_CFLAGS_OPTION], [
AC_MSG_CHECKING([whether ${CC} $2 is understood])
AC_LANG_SAVE
AC_LANG_C
SAVE_CFLAGS=$CFLAGS
CFLAGS=$2
AC_TRY_COMPILE(,[;],AC_MSG_RESULT([yes]); $1="${$1} $2",AC_MSG_RESULT([no]))
CFLAGS=$SAVE_CFLAGS
AC_LANG_RESTORE
])


dnl AX_CXXFLAGS_OPTIONS(var-name, option)
dnl add option to var-name if $CXX support it.
AC_DEFUN([AX_CXXFLAGS_OPTION], [
AC_MSG_CHECKING([whether ${CXX} $2 is understood])
AC_LANG_SAVE
AC_LANG_CPLUSPLUS
SAVE_CXXFLAGS=$CXXFLAGS
CXXFLAGS=$2
AC_TRY_COMPILE(,[;],AC_MSG_RESULT([yes]); $1="${$1} $2",AC_MSG_RESULT([no]))
CXXFLAGS=$SAVE_CXXFLAGS
AC_LANG_RESTORE
])

dnl AX_COPY_IF_CHANGE(source, dest)
dnl copy source to dest if they don't compare equally or if dest doesn't exist
AC_DEFUN([AX_COPY_IF_CHANGE], [
if test -r $2; then
	if cmp $1 $2 > /dev/null; then
		echo $2 is unchanged
	else
		cp -f $1 $2
	fi
else
	cp -f $1 $2
fi
])

