dnl Do not call GNOME_DOC_DEFINES directly.  It is split out from
dnl GNOME_DOC_INIT to allow gnome-doc-utils to bootstrap off itself.
AC_DEFUN([GNOME_DOC_DEFINES],
[
AC_ARG_WITH([help-dir],
  AC_HELP_STRING([--with-help-dir=DIR], [path to help docs]),,
  [with_help_dir='${datadir}/gnome/help'])
HELP_DIR="$with_help_dir"
AC_SUBST(HELP_DIR)

AC_ARG_WITH([omf-dir],
  AC_HELP_STRING([--with-omf-dir=DIR], [path to OMF files]),,
  [with_omf_dir='${datadir}/omf'])
OMF_DIR="$with_omf_dir"
AC_SUBST(OMF_DIR)

AC_ARG_WITH([help-formats],
  AC_HELP_STRING([--with-help-formats=FORMATS], [list of formats]),,
  [with_help_formats=''])
DOC_USER_FORMATS="$with_help_formats"
AC_SUBST(DOC_USER_FORMATS)

AC_ARG_ENABLE([scrollkeeper],
	[AC_HELP_STRING([--disable-scrollkeeper],
			[do not make updates to the scrollkeeper database])],,
	enable_scrollkeeper=yes)
AM_CONDITIONAL([ENABLE_SK],[test "$gdu_cv_have_gdu" = "yes" -a "$enable_scrollkeeper" = "yes"])

dnl disable scrollkeeper automatically for distcheck
DISTCHECK_CONFIGURE_FLAGS="--disable-scrollkeeper $DISTCHECK_CONFIGURE_FLAGS"
AC_SUBST(DISTCHECK_CONFIGURE_FLAGS)

AM_CONDITIONAL([HAVE_GNOME_DOC_UTILS],[test "$gdu_cv_have_gdu" = "yes"])
])

# GNOME_DOC_INIT ([MINIMUM-VERSION],[ACTION-IF-FOUND],[ACTION-IF-NOT-FOUND])
#
AC_DEFUN([GNOME_DOC_INIT],
[AC_REQUIRE([AC_PROG_LN_S])dnl

if test -z "$AM_DEFAULT_VERBOSITY"; then
  AM_DEFAULT_VERBOSITY=1
fi
AC_SUBST([AM_DEFAULT_VERBOSITY])

ifelse([$1],,[gdu_cv_version_required=0.3.2],[gdu_cv_version_required=$1])

AC_MSG_CHECKING([gnome-doc-utils >= $gdu_cv_version_required])
PKG_CHECK_EXISTS([gnome-doc-utils >= $gdu_cv_version_required],
	[gdu_cv_have_gdu=yes],[gdu_cv_have_gdu=no])

if test "$gdu_cv_have_gdu" = "yes"; then
	AC_MSG_RESULT([yes])
	ifelse([$2],,[:],[$2])
else
	AC_MSG_RESULT([no])
	ifelse([$3],,[:],[$3])
fi

GNOME_DOC_DEFINES
])
