# NOTE: THIS FILE IS DEPRECATED. Please add new entries to the recipes themselves.
#
# package_regex.inc - This file contains data that tracks
# upstream project associated with a given recipe. This list is needed
# for recipes that version information can not be automagically discovered.
# As we automate this checking, this file will slowly be reduced.
#"
# This data is used by the package reporting system (packages.yoctoproject.org)
#"
# The format is as a bitbake variable override for each recipe
#"
#       UPSTREAM_CHECK_URI_pn-<recipe name> = "recipe_url"
#	 - This is the url used by the package checking system to
#	   get the latest version of the package
#       UPSTREAM_CHECK_REGEX_pn-<recipe name> = "package_regex"
#	 - This is the regex the package checking system uses to
#	   parse the page found at UPSTREAM_CHECK_URI_pn-<recipe name>
#	UPSTREAM_CHECK_GITTAGREGEX_pn-<recipe name> = "git_tag_regex"
#	 - When source code is fetched from git, git tags are used to
#	   determine the upstream release version. This regex can be used
#	   to filter only relevant tags.
#
