/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel;

import static io.netty.util.internal.ObjectUtil.checkPositive;

import java.net.SocketAddress;

/**
 * Represents the properties of a {@link Channel} implementation.
 */
public final class ChannelMetadata {

    private final boolean hasDisconnect;
    private final int defaultMaxMessagesPerRead;

    /**
     * 创建一个新的实例
     *
     * @param hasDisconnect     {@code true} 有且仅有一个操作 {@code disconnect()} 允许用户关闭连接
     *                          然后调用 {@link Channel#connect(SocketAddress)} 再次连接。比如UDP/IP。
     */
    public ChannelMetadata(boolean hasDisconnect) {
        this(hasDisconnect, 1);
    }

    /**
     * 创建一个新的实例
     *
     * @param hasDisconnect     {@code true} 有且仅有一个操作 {@code disconnect()} 允许用户关闭连接
     *                          然后调用 {@link Channel#connect(SocketAddress)} 再次连接。比如UDP/IP。
     *
     * @param defaultMaxMessagesPerRead 如果 {@link MaxMessagesRecvByteBufAllocator} 缓存区被使用,
     * 这个值将被设置为 {@link MaxMessagesRecvByteBufAllocator#maxMessagesPerRead()}. 必须＞0 {@code > 0}.
     */
    public ChannelMetadata(boolean hasDisconnect, int defaultMaxMessagesPerRead) {
        checkPositive(defaultMaxMessagesPerRead, "defaultMaxMessagesPerRead");
        this.hasDisconnect = hasDisconnect;
        this.defaultMaxMessagesPerRead = defaultMaxMessagesPerRead;
    }

    /**
     *{@code true} 有且仅有一个操作 {@code disconnect()} 允许用户关闭连接
     * 然后调用 {@link Channel#connect(SocketAddress)} 再次连接。比如UDP/IP。
     */
    public boolean hasDisconnect() {
        return hasDisconnect;
    }

    /**
     * 如果 {@link MaxMessagesRecvByteBufAllocator} 缓存区被使用,
     * 这个值将被设置为 {@link MaxMessagesRecvByteBufAllocator#maxMessagesPerRead()}. 必须＞0 {@code > 0}.
     */
    public int defaultMaxMessagesPerRead() {
        return defaultMaxMessagesPerRead;
    }
}
