This layer depends on:

URI: git://git.openembedded.org/openembedded-core
branch: master

URI: git://git.openembedded.org/meta-openembedded
branch: master

meta-xfce depends on meta-oe, meta-gnome and meta-multimedia in this repository.

To avoid dependencies on meta-multimedia you need to mask recipes by adding
this to local.conf:

BBMASK = "meta-xfce/recipes-multimedia"

Send pull requests to openembedded-devel@lists.openembedded.org with '[meta-xfce]' in the subject'

When sending single patches, please use something like:
git send-email -M -1 --to openembedded-devel@lists.openembedded.org --subject-prefix='meta-xfce][wrynose][PATCH'

Layer maintainer: Anuj Mittal <anuj.mittal@oss.qualcomm.com>
