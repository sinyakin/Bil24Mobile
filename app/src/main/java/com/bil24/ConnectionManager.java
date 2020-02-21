package com.bil24;

public class ConnectionManager extends Thread {
  protected volatile boolean terminated = false;
  protected volatile boolean waking = false;
  private static final int RESPONSE = 1000; //допустимое время отклика потока в мс

  /**
   * Создает новый Connection Manager.
   */
  public ConnectionManager() {
    setPriority(3);
  }

  /**
   * Устанавливает флаг завершения потока. При первой возможности поток будет
   * корректно завершен.
   */
  public void terminate() {
    terminated = true;
  }

  /**
   * Устанавливает флаг пробуждения потока. В течение времени отклика поток
   * будет разбужен.
   */
  public void wakeup() {
    waking = true;
  }


  /**
   * Приостанавливает поток на заданное время. Пауза может быть снята методами
   * terminate() или wakeup().
   * @param   delay  Время приостановки потока в мс.
   */
  protected void pause(int delay) {
    int ticks = 0;
    delay = delay / RESPONSE;
    while ((ticks++ < delay) && !waking && !terminated) {
      try { sleep(RESPONSE); }
      catch (InterruptedException ex) {System.err.println("ConnectionManager Interrupted");}
    }
    waking = false;
  }
}
