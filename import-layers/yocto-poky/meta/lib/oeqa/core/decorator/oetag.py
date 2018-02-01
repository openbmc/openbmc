# Copyright (C) 2016 Intel Corporation
# Released under the MIT license (see COPYING.MIT)

from . import OETestFilter, registerDecorator
from oeqa.core.utils.misc import strToList

def _tagFilter(tags, filters):
    return False if set(tags) & set(filters) else True

@registerDecorator
class OETestTag(OETestFilter):
    attrs = ('oetag',)

    def bind(self, registry, case):
        super(OETestTag, self).bind(registry, case)
        self.oetag = strToList(self.oetag, 'oetag')

    def filtrate(self, filters):
        if filters.get('oetag'):
            filterx = strToList(filters['oetag'], 'oetag')
            del filters['oetag']
            if _tagFilter(self.oetag, filterx):
                return True
        return False
