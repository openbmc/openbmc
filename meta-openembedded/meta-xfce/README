This layer depends on:

URI: git://github.com/openembedded/oe-core.git
branch: warrior
revision: HEAD

URI: git://github.com/openembedded/meta-oe.git
branch: warrior
revision: HEAD
meta-xfce depends on meta-oe, meta-gnome and meta-multimedia in this repository.

To avoid dependencies on meta-multimedia you need to mask recipes by adding
this to local.conf:

BBMASK = "meta-xfce/recipes-multimedia"

Send pull requests to openembedded-devel@lists.openembedded.org with '[meta-xfce][warrior]' in the subject'

When sending single patches, please using something like:
'git send-email -M -1 --to openembedded-devel@lists.openembedded.org --subject-prefix=meta-xfce][warrior][PATCH'

Warrior Maintainer: Armin Kuster <akuster808@gmail.com>
