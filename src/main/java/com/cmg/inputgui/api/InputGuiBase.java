package com.cmg.inputgui.api;

public abstract interface InputGuiBase<T>
{
  public abstract T getDefaultText();
  
  public abstract void onConfirm(InputPlayer paramInputPlayer, T paramT);
  
  public abstract void onCancel(InputPlayer paramInputPlayer);
}
