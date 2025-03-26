package com.evo.middleware.audit.context.variable;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 变量上下文
 */
public abstract class VariableContext implements ExpressionContext{
	protected static final InheritableThreadLocal<ConcurrentHashMap<String,Stack<Map<String, Object>>>> variableMapStacks = new InheritableThreadLocal<>();

	protected abstract String getKey();

	private void initStacks() {
		String key = getKey();
		ConcurrentHashMap<String, Stack<Map<String, Object>>> stacks
				= variableMapStacks.get();
		if (stacks == null) {
			stacks = new ConcurrentHashMap<>();
			stacks.put(key, new Stack<>());
			variableMapStacks.set(stacks);
		}
        variableMapStacks.get().computeIfAbsent(key, k -> new Stack<>());
	}

	private Stack<Map<String, Object>> getStack() {
		String key = getKey();
		ConcurrentHashMap<String, Stack<Map<String, Object>>> stacks
				= variableMapStacks.get();
		if (stacks == null){
			initStacks();
			stacks = variableMapStacks.get();
		}
		Stack<Map<String, Object>> stack = stacks.get(key);
		if (stack == null){
			variableMapStacks.get().put(key, new Stack<>());
			stack = variableMapStacks.get().get(key);
		}
		if (stack.isEmpty()) {
			variableMapStacks.get().get(key).push(new HashMap<>());
		}
		return stack;
	}

	private void setVariableMap(HashMap<String, Object> map) {
		String key = getKey();
		ConcurrentHashMap<String, Stack<Map<String, Object>>> stacks = variableMapStacks.get();
		if (stacks == null){
			initStacks();
			stacks = variableMapStacks.get();
		}
		Stack<Map<String, Object>> stack = stacks.get(key);
		if (stack == null){
			variableMapStacks.get().put(key, new Stack<>());
		}
		variableMapStacks.get().get(key).push(map);
	}

	private void setVariable(String name, Object value) {
		String key = getKey();
		variableMapStacks.get().get(key).peek().put(name, value);
	}
	/**
	 * 日志使用方不需要使用到这个方法
	 * 每进入一个方法初始化一个 span 放入到 stack中，方法执行完后 pop 掉这个span
	 */
	public void putEmptySpan() {
		initStacks();
		setVariableMap(new HashMap<>());
	}

	public void putVariable(String name, Object value) {
		initStacks();
		Stack<Map<String, Object>> mapStack = getStack();
		if (mapStack.isEmpty()) {
			setVariableMap(new HashMap<>());
		}
		setVariable(name, value);
	}

	public Map<String, Object> getVariables() {
		Stack<Map<String, Object>> mapStack = getStack();
		return mapStack.peek();
	}

	public void clear() {
		String key = getKey();
		Map<String, Stack<Map<String, Object>>> stacks = variableMapStacks.get();
		if (stacks == null){
			return;
		}
		Stack<Map<String, Object>> stack = stacks.get(key);
		if (stack != null) {
			variableMapStacks.get().get(key).pop();
			// stack 空了 则删除当前Map
			if (variableMapStacks.get().get(key).empty()) {
				variableMapStacks.get().remove(key);
			}
			// stackMap 空了 则删除当前ThreadLocal
			if (variableMapStacks.get().isEmpty()){
				variableMapStacks.remove();
			}
		}
	}
}
