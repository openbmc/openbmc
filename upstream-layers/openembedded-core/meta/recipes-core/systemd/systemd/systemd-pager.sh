# Systemd expect a color capable pager, however the less provided
# by busybox is not. This make many interaction with systemd pretty
# annoying. As a workaround we disable the systemd pager if less
# is not the GNU version.
if ! less -V > /dev/null 2>&1 ; then
	export SYSTEMD_PAGER=
fi
