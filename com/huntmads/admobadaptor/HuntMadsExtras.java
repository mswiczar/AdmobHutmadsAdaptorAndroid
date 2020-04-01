
package com.huntmads.admobadaptor;

import com.google.ads.mediation.NetworkExtras;

/**
 * Skeleton Adapter Extra Parameters
 */
public final class HuntMadsExtras implements NetworkExtras {
  /*
   * This class holds all of the "extra" parameters a publisher can specify that aren't explicitly
   * included in an AdRequest.  New parameters can be added here and when specified by publishers
   * will be passed through to the Adapter.
   *
   * When the user specifies this data, it is keyed by the class.  Because of this each adapter
   * should define it's own class for extras, rather than use a generic container class.  This also
   * makes it easier for the publisher to discover these fields.
   *
   * It's strongly suggested that this class be final so end-users don't accidentally subclass it.
   */



  /*
   * ------------------------------------------------------------------------------
   * BoneType (enum, get, set, clear)
   * ------------------------------------------------------------------------------
   */

  /**
   * Enum representing type of bone in skeleton
   */
  public enum BoneType {
    TIBIA,
    FEMUR,
    PATELLA,
    OTHER,
    UNKNOWN,
  }

  
  
  private BoneType boneType = BoneType.UNKNOWN;

  /**
   * Sets location of ad on screen
   */
  public HuntMadsExtras setBoneType(BoneType boneType) {
    this.boneType = boneType;
    return this;
  }

  /**
   * Clears location of ad on screen
   */
  public HuntMadsExtras clearBoneType() {
    return setBoneType(null);
  }

  /**
   * Gets location of ad on screen if set
   */
  public BoneType getAdLocation() {
    return this.boneType;
  }
}
