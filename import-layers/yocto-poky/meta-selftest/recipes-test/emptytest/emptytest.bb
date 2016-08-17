include test_recipe.inc

# Set LICENSE to something so that bitbake -p that is ran at the beginning
# is successful since test_recipe.inc has not yet been modified.
LICENSE = ""

EXCLUDE_FROM_WORLD = "1"
