# NOTE: THIS FILE IS DEPRECATED. Please add new entries to the recipes themselves.
#
# upstream_tracking.inc - This file contains data that tracks the
# upstream project associated with a given recipe. This list is needed
# for recipes that version information can not be automagically discovered.
# As we automate this checking, this file will slowed be reduced.
#
# This data is used by the package reporting system (packages.yoctoproject.org)
#
# The format is as a bitbake variable override for each recipe
#
#       RECIPE_UPSTREAM_VERSION_pn-<recipe name> = "<version>"
#       RECIPE_UPSTREAM_DATE_pn-<recipe name> = "Mmm dd, YYYY"
#	 - This is the latest upstream version and date
#       RECIPE_NO_UPDATE_REASON_pn-<recipe name> = "<free form text>"
#        - Used to note why a recipe should not be upgraded to a newer versoin
#       CHECK_DATE_pn-<recipe name> = "Mmm dd, YYYY"
#        - This is the date that upstream was last manually checked over time
#          this should be removed as we automate this checking
#
# These entries will slowly be removed as we can automatically determine
# the information in this file.  So for existing recipes that we can get 
# from the Recipe reporting system, the entries here can be removed.
#
