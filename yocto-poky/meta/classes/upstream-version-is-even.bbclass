# This class ensures that the upstream version check only
# accepts even minor versions (i.e. 3.0.x, 3.2.x, 3.4.x, etc.)
# This scheme is used by Gnome and a number of other projects
# to signify stable releases vs development releases.
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+\.(\d*[02468])+(\.\d+)+)"
