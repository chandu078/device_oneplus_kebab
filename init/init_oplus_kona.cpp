/*
 * Copyright (C) 2022 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

#include <android-base/logging.h>
#include <android-base/properties.h>

#define _REALLY_INCLUDE_SYS__SYSTEM_PROPERTIES_H_
#include <stdio.h>
#include <stdlib.h>
#include <sys/sysinfo.h>
#include <sys/system_properties.h>
#include <sys/_system_properties.h>

#include "property_service.h"
#include "vendor_init.h"

using android::base::GetProperty;
using std::string;

std::vector<std::string> ro_props_default_source_order = {
    "",
    "odm.",
    "product.",
    "system.",
    "system_ext.",
    "vendor.",
    "vendor_dlkm."
};

/*
 * SetProperty does not allow updating read only properties and as a result
 * does not work for our use case. Write "property_override" to do practically
 * the same thing as "SetProperty" without this restriction.
 */

void property_override(char const prop[], char const value[], bool add = true) {
    prop_info *pi;

    pi = (prop_info*) __system_property_find(prop);
    if (pi)
        __system_property_update(pi, value, strlen(value));
    else if (add)
        __system_property_add(prop, strlen(prop), value, strlen(value));
}

void set_ro_build_prop(const std::string &prop, const std::string &value, bool product = true) {
    string prop_name;

    for (const auto &source : ro_props_default_source_order) {
        if (product)
            prop_name = "ro.product." + source + prop;
        else
            prop_name = "ro." + source + "build." + prop;

        property_override(prop_name.c_str(), value.c_str());
    }
}

void vendor_load_properties() {

  string model;
  string device;
  string name;

/*
 * Only for read-only properties. Properties that can be wrote to more
 * than once should be set in a typical init script (e.g. init.oplus.hw.rc)
 * after the original property has been set.
 */

  auto prj_version = std::stoi(GetProperty("ro.boot.prj_version", "0"));
  auto rf_version = std::stoi(GetProperty("ro.boot.rf_version", "0"));

  switch(prj_version){
       /* OnePlus 8T */
    case 19805:
          device = "OnePlus8T";
      switch (rf_version){
           /* China */
        case 11:
          name = "OnePlus8T_CN";
          model = "KB2000";
          break;
            /* India */
        case 13:
          name = "OnePlus8T_IN";
          model = "KB2001";
          break;
            /* Europe */
        case 14:
          name = "OnePlus8T_EU";
          model = "KB2003";
          break;
            /* Global / US Unlocked */
        case 15:
          name = "OnePlus8T_NA";
          model = "KB2005";
          break;
            /* Generic */
        default:
          name = "OnePlus8T_NA";
          model = "KB2005";
          break;
      }
      break;
       /* OnePlus 8T T-Mobile */
    case 20809:
          device = "OnePlus8T";
      switch (rf_version){
            /* T-Mobile */
        case 12:
          name = "OnePlus8T_TMO";
          model = "KB2007";
          break;
            /* Generic */
        default:
          name = "OnePlus8T_NA";
          model = "KB2005";
          break;
      }
      break;
       /* OnePlus 9R */
    case 20828:
          device = "OnePlus9R";
            /* Override usb name to OnePlus 9R */
          property_override("vendor.usb.product_string", "OnePlus 9R");
      switch (rf_version){
            /* China */
      case 11:
          name = "OnePlus9R_CN";
          model = "LE2100";
          break;
            /* India */
      case 13:
          name = "OnePlus9R_IN";
          model = "LE2101";
          break;
            /* Generic */
      default:
          name = "OnePlus9R_IN";
          model = "LE2101";
          break;
      }
      break;
  }

    set_ro_build_prop("device", device);
    set_ro_build_prop("model", model);
    set_ro_build_prop("name", name);
    set_ro_build_prop("product", model, false);
}
