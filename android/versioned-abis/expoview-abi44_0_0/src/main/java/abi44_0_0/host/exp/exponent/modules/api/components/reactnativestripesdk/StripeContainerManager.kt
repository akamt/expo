package abi44_0_0.host.exp.exponent.modules.api.components.reactnativestripesdk

import abi44_0_0.com.facebook.react.uimanager.ThemedReactContext
import abi44_0_0.com.facebook.react.uimanager.ViewGroupManager
import abi44_0_0.com.facebook.react.uimanager.annotations.ReactProp

class StripeContainerManager : ViewGroupManager<StripeContainerView>() {
  override fun getName() = "StripeContainer"

  @ReactProp(name = "keyboardShouldPersistTaps")
  fun setKeyboardShouldPersistTaps(view: StripeContainerView, keyboardShouldPersistTaps: Boolean) {
    view.setKeyboardShouldPersistTaps(keyboardShouldPersistTaps)
  }

  override fun createViewInstance(reactContext: ThemedReactContext): StripeContainerView {
    return StripeContainerView(reactContext)
  }
}
