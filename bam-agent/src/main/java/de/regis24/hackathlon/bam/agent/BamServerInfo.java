package de.regis24.hackathlon.bam.agent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author sandornemeth
 */
@Getter
@Setter
@AllArgsConstructor
public class BamServerInfo {

  private String host;
  private int port;

}
