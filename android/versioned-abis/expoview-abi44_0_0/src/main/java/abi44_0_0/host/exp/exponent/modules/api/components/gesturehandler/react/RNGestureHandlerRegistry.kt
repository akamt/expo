package abi44_0_0.host.exp.exponent.modules.api.components.gesturehandler.react

import android.util.SparseArray
import android.view.View
import abi44_0_0.com.facebook.react.bridge.UiThreadUtil
import abi44_0_0.host.exp.exponent.modules.api.components.gesturehandler.GestureHandler
import abi44_0_0.host.exp.exponent.modules.api.components.gesturehandler.GestureHandlerRegistry
import java.util.*

class RNGestureHandlerRegistry : GestureHandlerRegistry {
  private val handlers = SparseArray<GestureHandler<*>>()
  private val attachedTo = SparseArray<Int?>()
  private val handlersForView = SparseArray<ArrayList<GestureHandler<*>>>()

  @Synchronized
  fun registerHandler(handler: GestureHandler<*>) {
    handlers.put(handler.tag, handler)
  }

  @Synchronized
  fun getHandler(handlerTag: Int): GestureHandler<*>? {
    return handlers[handlerTag]
  }

  @Synchronized
  fun attachHandlerToView(handlerTag: Int, viewTag: Int, useDeviceEvents: Boolean = false): Boolean {
    val handler = handlers[handlerTag]
    return handler?.let {
      detachHandler(handler)
      handler.usesDeviceEvents = useDeviceEvents
      registerHandlerForViewWithTag(viewTag, handler)
      true
    } ?: false
  }

  @Synchronized
  private fun registerHandlerForViewWithTag(viewTag: Int, handler: GestureHandler<*>) {
    check(attachedTo[handler.tag] == null) { "Handler $handler already attached" }
    attachedTo.put(handler.tag, viewTag)
    var listToAdd = handlersForView[viewTag]
    if (listToAdd == null) {
      listToAdd = ArrayList(1)
      listToAdd.add(handler)
      handlersForView.put(viewTag, listToAdd)
    } else {
      listToAdd.add(handler)
    }
  }

  @Synchronized
  private fun detachHandler(handler: GestureHandler<*>) {
    val attachedToView = attachedTo[handler.tag]
    if (attachedToView != null) {
      attachedTo.remove(handler.tag)
      val attachedHandlers = handlersForView[attachedToView]
      if (attachedHandlers != null) {
        attachedHandlers.remove(handler)
        if (attachedHandlers.size == 0) {
          handlersForView.remove(attachedToView)
        }
      }
    }
    if (handler.view != null) {
      // Handler is in "prepared" state which means it is registered in the orchestrator and can
      // receive touch events. This means that before we remove it from the registry we need to
      // "cancel" it so that orchestrator does no longer keep a reference to it.
      UiThreadUtil.runOnUiThread { handler.cancel() }
    }
  }

  @Synchronized
  fun dropHandler(handlerTag: Int) {
    handlers[handlerTag]?.let {
      detachHandler(it)
      handlers.remove(handlerTag)
    }
  }

  @Synchronized
  fun dropAllHandlers() {
    handlers.clear()
    attachedTo.clear()
    handlersForView.clear()
  }

  @Synchronized
  fun getHandlersForViewWithTag(viewTag: Int): ArrayList<GestureHandler<*>>? {
    return handlersForView[viewTag]
  }

  @Synchronized
  override fun getHandlersForView(view: View): ArrayList<GestureHandler<*>>? {
    return getHandlersForViewWithTag(view.id)
  }
}
