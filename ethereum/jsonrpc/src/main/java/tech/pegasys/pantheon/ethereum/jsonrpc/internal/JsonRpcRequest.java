/*
 * Copyright 2018 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package tech.pegasys.pantheon.ethereum.jsonrpc.internal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.exception.InvalidJsonRpcRequestException;

import java.util.Arrays;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonRpcRequest {

  private JsonRpcRequestId id;
  private final String method;
  private final Object[] params;
  private final String version;
  private boolean isNotification = true;

  @JsonCreator
  public JsonRpcRequest(
      @JsonProperty("jsonrpc") final String version,
      @JsonProperty("method") final String method,
      @JsonProperty("params") final Object[] params) {
    this.version = version;
    this.method = method;
    this.params = params;
    if (method == null) {
      throw new InvalidJsonRpcRequestException("Field 'method' is required");
    }
  }

  @JsonGetter("id")
  public Object getId() {
    return id == null ? null : id.getValue();
  }

  @JsonGetter("method")
  public String getMethod() {
    return method;
  }

  @JsonGetter("jsonrpc")
  public String getVersion() {
    return version;
  }

  @JsonInclude(Include.NON_NULL)
  @JsonFormat(with = Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
  @JsonGetter("params")
  public Object[] getParams() {
    return params;
  }

  @JsonIgnore
  public boolean isNotification() {
    return isNotification;
  }

  @JsonIgnore
  public int getParamLength() {
    return hasParams() ? params.length : 0;
  }

  @JsonIgnore
  public boolean hasParams() {

    // Null Object: "params":null
    if (params == null) {
      return false;
    }

    // Null Array: "params":[null]
    if (params.length == 0 || params[0] == null) {
      return false;
    }

    return true;
  }

  @JsonSetter("id")
  protected void setId(final JsonRpcRequestId id) {
    // If an id is explicitly set, it is not a notification
    isNotification = false;
    this.id = id;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final JsonRpcRequest that = (JsonRpcRequest) o;
    return isNotification == that.isNotification
        && Objects.equals(id, that.id)
        && Objects.equals(method, that.method)
        && Arrays.equals(params, that.params)
        && Objects.equals(version, that.version);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, method, Arrays.hashCode(params), version, isNotification);
  }
}
