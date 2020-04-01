// Copyright 2011 Google Inc. All Rights Reserved.

package com.huntmads.admobadaptor;

import com.google.ads.mediation.MediationServerParameters;

/**
 * Settings for Skeleton adapter from mediation backend
 */
public class HuntMadsServerParameters extends MediationServerParameters {
  /*
   * This class can either override load(Map<String, String>) or can provide String fields
   * with an @Parameter annotation.  Optional parameters can be specified in the annotation with
   * required = false.  If any required parameters are missing from the server, this adapter
   * will be skipped.
   */

	
  /**
   * Skeleton ID Number
   */
//  @Parameter(name = "skeletonId")
 // public String skeletonIdNumber;
  

  /**
   * Site
   */
  @Parameter(name = "siteID")
  public String IdSite;

  /**
   * x
   */
  @Parameter(name = "zoneID")
  public String IdZone;

  
  
  
  


  
  
  
}
