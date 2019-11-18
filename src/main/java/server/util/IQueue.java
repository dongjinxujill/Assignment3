package server.util;

import java.util.List;

public interface IQueue<T>{
  void enqueue(T t);
  List<T> dequeue(int num);
}
