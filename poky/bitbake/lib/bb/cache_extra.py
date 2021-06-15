#
# Extra RecipeInfo will be all defined in this file. Currently,
# Only Hob (Image Creator) Requests some extra fields. So
# HobRecipeInfo is defined. It's named HobRecipeInfo because it
# is introduced by 'hob'. Users could also introduce other
# RecipeInfo or simply use those already defined RecipeInfo.
# In the following patch, this newly defined new extra RecipeInfo
# will be dynamically loaded and used for loading/saving the extra
# cache fields  

# Copyright (C) 2011, Intel Corporation. All rights reserved.

# SPDX-License-Identifier: GPL-2.0-only
#

from bb.cache import RecipeInfoCommon

class HobRecipeInfo(RecipeInfoCommon):
    __slots__ = ()

    classname = "HobRecipeInfo"
    # please override this member with the correct data cache file
    # such as (bb_cache.dat, bb_extracache_hob.dat) 
    cachefile = "bb_extracache_" + classname +".dat"        

    # override this member with the list of extra cache fields
    # that this class will provide
    cachefields = ['summary', 'license', 'section',
            'description', 'homepage', 'bugtracker',
            'prevision', 'files_info']

    def __init__(self, filename, metadata):

        self.summary = self.getvar('SUMMARY', metadata)
        self.license = self.getvar('LICENSE', metadata)
        self.section = self.getvar('SECTION', metadata)
        self.description = self.getvar('DESCRIPTION', metadata)
        self.homepage = self.getvar('HOMEPAGE', metadata)
        self.bugtracker = self.getvar('BUGTRACKER', metadata)
        self.prevision = self.getvar('PR', metadata)
        self.files_info = self.getvar('FILES_INFO', metadata)

    @classmethod
    def init_cacheData(cls, cachedata):
        # CacheData in Hob RecipeInfo Class
        cachedata.summary = {}
        cachedata.license = {}
        cachedata.section = {}
        cachedata.description = {}
        cachedata.homepage = {}
        cachedata.bugtracker = {}
        cachedata.prevision = {}
        cachedata.files_info = {}

    def add_cacheData(self, cachedata, fn):
        cachedata.summary[fn] = self.summary
        cachedata.license[fn] = self.license
        cachedata.section[fn] = self.section
        cachedata.description[fn] = self.description
        cachedata.homepage[fn] = self.homepage
        cachedata.bugtracker[fn] = self.bugtracker
        cachedata.prevision[fn] = self.prevision
        cachedata.files_info[fn] = self.files_info
