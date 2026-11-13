This layer depends on:

URI: git://git.openembedded.org/openembedded-core
branch: blacksail

URI: git://git.openembedded.org/meta-openembedded
branch: blacksail

meta-xfce depends on meta-oe, meta-gnome and meta-multimedia in this repository.

To avoid dependencies on meta-multimedia you need to mask recipes by adding
this to local.conf:

BBMASK = "meta-xfce/recipes-multimedia"

Send pull requests to openembedded-devel@lists.openembedded.org with '[meta-xfce][blacksail]' in the subject'

When sending single patches, please use something like:
git send-email -M -1 --to openembedded-devel@lists.openembedded.org --subject-prefix='meta-xfce][blacksail][PATCH'

Layer maintainer: Ankur Tyagi <ankur.tyagi85@gmail.com>
