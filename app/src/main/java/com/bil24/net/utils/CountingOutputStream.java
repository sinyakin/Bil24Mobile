/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bil24.net.utils;

import java.io.OutputStream;

/**
 * A decorating output stream that counts the number of bytes that have passed
 * through the stream so far.
 * <p/>
 * A typical use case would be during debugging, to ensure that data is being
 * written as expected.
 */
public class CountingOutputStream extends ProxyOutputStream {

  /**
   * The count of bytes that have passed.
   */
  private long count = 0;

  /**
   * Constructs a new CountingOutputStream.
   *
   * @param out the OutputStream to write to
   */
  public CountingOutputStream(OutputStream out) {
    super(out);
  }

  /**
   * Constructs a new CountingOutputStream.
   *
   * @param out  the OutputStream to write to
   * @param init init counter
   */
  public CountingOutputStream(OutputStream out, long init) {
    super(out);
    count = init;
  }

  //-----------------------------------------------------------------------

  /**
   * Updates the count with the number of bytes that are being written.
   *
   * @param n number of bytes to be written to the stream
   * @since 2.0
   */
  @Override
  protected synchronized void beforeWrite(int n) {
    count += n;
  }

  //-----------------------------------------------------------------------

  /**
   * The number of bytes that have passed through this stream.
   * <p/>
   * NOTE: This method is an alternative for <code>getCount()</code>.
   * It was added because that method returns an integer which will
   * result in incorrect count for files over 2GB.
   *
   * @return the number of bytes accumulated
   * @since 1.3
   */
  public synchronized long getByteCount() {
    return this.count;
  }

  /**
   * Set the byte count back to 0.
   * <p/>
   * NOTE: This method is an alternative for <code>resetCount()</code>.
   * It was added because that method returns an integer which will
   * result in incorrect count for files over 2GB.
   *
   * @return the count previous to resetting
   * @since 1.3
   */
  public synchronized long resetByteCount() {
    long tmp = this.count;
    this.count = 0;
    return tmp;
  }

}
