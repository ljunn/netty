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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeMap;

import java.net.InetSocketAddress;
import java.net.SocketAddress;


/**
 * 与网络套接字或者能够读、写、连接、绑定等IO操作组件的连接。
 * <p>
 * Channel提供的功能：
 * <ul>
 * <li>当前Channel的状态（如是否连接）</li>
 * <li> Channel的{@linkplain ChannelConfig 配置参数} (如. 接受区缓存大小),</li>
 * <li>Channel提供的IO操作 (e.g. 读, 写, 连接, 和 绑定), and</li>
 * <li>管道 {@link ChannelPipeline} 处理所有的IO事件 并且 请求连接通道</li>
 * </ul>
 *
 * <h3>所有的IO操作都是异步的</h3>
 * <p>
 * Netty中所有的IO操作都是异步的。
 * 这意味着所有的IO操作都会立即返回，但是不保证返回时IO操作已经结束。
 * 相反, 将会返回一个实例
 *  {@link ChannelFuture} ，该实例将在请求的IO操作成功、失败或者取消时通知您。
 *
 * <h3>Channels 是分层次的。</h3>
 * <p>
 * 一个 {@link Channel} 可以拥有一个 {@linkplain #parent() parent} 父类， 这取决于他是如何创建的。
 * 比如, 一个 {@link SocketChannel}, 绑定在 {@link ServerSocketChannel},
 * 将会把 {@link ServerSocketChannel} 当成父Channel {@link #parent()}.
 * <p>
 * Channel的层次结构取决于Channel的传输实现。
 * 比如, 你可以写一个新的 {@link Channel} 实现，创建新的子Channel并且分享连接
 * as <a href="http://beepcore.org/">BEEP</a> and
 * <a href="https://en.wikipedia.org/wiki/Secure_Shell">SSH</a> do.
 *
 * <h3> 子类型实现特定操作 </h3>
 * <p>
 *  有些IO需要特定的操作，可以实现子类去调用特定操作。
 *  比如IO的数据结构, 实现 {@link DatagramChannel}.
 *
 * <h3>释放资源</h3>
 * <p>
 * 当你结束使用{@link Channel}时， 调用 {@link #close()} 或者 {@link #close(ChannelPromise)}  去释放资源非常重要。
 * 它确保你正确释放了资源。 如 filehandles
 */
public interface Channel extends AttributeMap, ChannelOutboundInvoker, Comparable<Channel> {

    /**
     * 返回全局唯一标识 {@link Channel}.
     */
    ChannelId id();

    /**
     * 返回这个Channel注册的事件循环 {@link EventLoop}
     */
    EventLoop eventLoop();

    /**
     * 返回当前Channel的父容器
     *
     * @return the parent channel.
     *         {@code null} 没有父容器则返回空
     */
    Channel parent();

    /**
     * 返回当前Channel的配置信息
     */
    ChannelConfig config();

    /**
     * 返回当前Channel是否打开，或者即将打开。
     */
    boolean isOpen();

    /**
     * 返回当前Channel是否注册了EvenLoop
     */
    boolean isRegistered();

    /**
     * 返回当前Channel是否活跃
     */
    boolean isActive();

    /**
     * 返回Channel元数据 {@link ChannelMetadata}  which describe the nature of the {@link Channel}.
     */
    ChannelMetadata metadata();

    /**
     * 返回Channel绑定的本地地址.
     * {@link SocketAddress} 这个类可以用各种子类实现如{@link InetSocketAddress}来保持详细数据。
     *
     * @return Channel本地地址.
     *         {@code null} if this channel is not bound.
     */
    SocketAddress localAddress();

    /**
     * 返回当前Channel连接的远程地址。  The
     * returned {@link SocketAddress} is supposed to be down-cast into more
     * concrete type such as {@link InetSocketAddress} to retrieve the detailed
     * information.
     *
     * @return the remote address of this channel.
     *         {@code null} if this channel is not connected.
     *         If this channel is not connected but it can receive messages
     *         from arbitrary remote addresses (e.g. {@link DatagramChannel},
     *         use {@link DatagramPacket#recipient()} to determine
     *         the origination of the received message as this method will
     *         return {@code null}.
     */
    SocketAddress remoteAddress();

    /**
     * 返回 {@link ChannelFuture} ，当channel被关闭时返回数据。
     * 这个方法总是会返回同一个Future实例。
     */
    ChannelFuture closeFuture();

    /**
     * 只会在IO线程立即响应写请求时返回 {@code true}
     */
    boolean isWritable();

    /**
     * 获取在isWritable返回false之前可以写入的字节数.
     * 如果 {@link #isWritable()} 是 {@code false} 返回 0.
     */
    long bytesBeforeUnwritable();

    /**
     * 获取必须从缓存区中释放多少字节
     */
    long bytesBeforeWritable();

    /**
     * 返回一个仅供内部使用的对象来支持不安全的操作
     */
    Unsafe unsafe();

    /**
     * 返回分配的管道 {@link ChannelPipeline}.
     */
    ChannelPipeline pipeline();

    /**
     * 返回分配的缓存区 {@link ByteBufAllocator} which will be used to allocate {@link ByteBuf}s.
     */
    ByteBufAllocator alloc();

    @Override
    Channel read();

    @Override
    Channel flush();

    /**
     * <em>Unsafe</em> operations that should <em>never</em> be called from user-code. These methods
     * are only provided to implement the actual transport, and must be invoked from an I/O thread except for the
     * following methods:
     * <ul>
     *   <li>{@link #localAddress()}</li>
     *   <li>{@link #remoteAddress()}</li>
     *   <li>{@link #closeForcibly()}</li>
     *   <li>{@link #register(EventLoop, ChannelPromise)}</li>
     *   <li>{@link #deregister(ChannelPromise)}</li>
     *   <li>{@link #voidPromise()}</li>
     * </ul>
     */
    interface Unsafe {

        /**
         * Return the assigned {@link RecvByteBufAllocator.Handle} which will be used to allocate {@link ByteBuf}'s when
         * receiving data.
         */
        RecvByteBufAllocator.Handle recvBufAllocHandle();

        /**
         * Return the {@link SocketAddress} to which is bound local or
         * {@code null} if none.
         */
        SocketAddress localAddress();

        /**
         * Return the {@link SocketAddress} to which is bound remote or
         * {@code null} if none is bound yet.
         */
        SocketAddress remoteAddress();

        /**
         * Register the {@link Channel} of the {@link ChannelPromise} and notify
         * the {@link ChannelFuture} once the registration was complete.
         */
        void register(EventLoop eventLoop, ChannelPromise promise);

        /**
         * Bind the {@link SocketAddress} to the {@link Channel} of the {@link ChannelPromise} and notify
         * it once its done.
         */
        void bind(SocketAddress localAddress, ChannelPromise promise);

        /**
         * Connect the {@link Channel} of the given {@link ChannelFuture} with the given remote {@link SocketAddress}.
         * If a specific local {@link SocketAddress} should be used it need to be given as argument. Otherwise just
         * pass {@code null} to it.
         *
         * The {@link ChannelPromise} will get notified once the connect operation was complete.
         */
        void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise);

        /**
         * Disconnect the {@link Channel} of the {@link ChannelFuture} and notify the {@link ChannelPromise} once the
         * operation was complete.
         */
        void disconnect(ChannelPromise promise);

        /**
         * Close the {@link Channel} of the {@link ChannelPromise} and notify the {@link ChannelPromise} once the
         * operation was complete.
         */
        void close(ChannelPromise promise);

        /**
         * Closes the {@link Channel} immediately without firing any events.  Probably only useful
         * when registration attempt failed.
         */
        void closeForcibly();

        /**
         * Deregister the {@link Channel} of the {@link ChannelPromise} from {@link EventLoop} and notify the
         * {@link ChannelPromise} once the operation was complete.
         */
        void deregister(ChannelPromise promise);

        /**
         * Schedules a read operation that fills the inbound buffer of the first {@link ChannelInboundHandler} in the
         * {@link ChannelPipeline}.  If there's already a pending read operation, this method does nothing.
         */
        void beginRead();

        /**
         * Schedules a write operation.
         */
        void write(Object msg, ChannelPromise promise);

        /**
         * Flush out all write operations scheduled via {@link #write(Object, ChannelPromise)}.
         */
        void flush();

        /**
         * Return a special ChannelPromise which can be reused and passed to the operations in {@link Unsafe}.
         * It will never be notified of a success or error and so is only a placeholder for operations
         * that take a {@link ChannelPromise} as argument but for which you not want to get notified.
         */
        ChannelPromise voidPromise();

        /**
         * Returns the {@link ChannelOutboundBuffer} of the {@link Channel} where the pending write requests are stored.
         */
        ChannelOutboundBuffer outboundBuffer();
    }
}
