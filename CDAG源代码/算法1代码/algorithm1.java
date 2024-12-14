// 对相同traceID的数据进行处理，排序+栈操作，得到调用关系
private static void processEntries(List<LogEntry> entries) {
        if (!entries.isEmpty()) {
        // 对当前traceID下的LogEntry列表根据eoi进行排序
        Collections.sort(entries, Comparator.comparingLong(entry -> entry.eoi));

        Stack<LogEntry> stack = new Stack<>();

        // 处理排序后的LogEntry列表
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath, true))) {
        for (LogEntry entry : entries) {
        if (!stack.isEmpty()) {
        LogEntry top = stack.peek();
        if (entry.ess <= top.ess) {      //【情况： 4;2之后5;1】  entry.ess<=栈顶元素的ess => 记录调用关系 => 弹出栈顶元素+入栈新元素
        while (!stack.isEmpty() && entry.ess <= stack.peek().ess) {
        stack.pop();
        }
        if (stack.isEmpty()) break;
        String str = stack.peek().methodName + "###" + entry.methodName;
//                        String str = stack.peek().eoi + ";" + stack.peek().ess + "###" + entry.eoi + ";" + entry.ess;

        // 将内容写入文件
        writer.write(str + "\n");

//                            System.out.println(str);
        stack.push(entry);

        } else {    // 与栈顶元素的ess不同  => 记录调用关系  =>  新元素入栈
        String str = top.methodName + "###" + entry.methodName;
//                        String str = top.eoi + ";" + top.ess + "###" + entry.eoi + ";" + entry.ess;
        // 将内容写入文件
        writer.write(str + "\n");
        System.out.println(str);
        stack.push(entry);

        }
        } else {
        stack.push(entry);   // 0,0入栈
        }
        }
        stack.clear();
        System.out.println();
        writer.flush();
        } catch (IOException e) {
        e.printStackTrace();
        }
        }
        }

// 每条记录的结构，只保留了方法名、traceID、eoi、ess
static class LogEntry {
    String methodName;
    String traceID;
    long eoi;   // 执行顺序
    long ess;   // 栈深度

    public LogEntry(String methodName, String traceID, long eoi, long ess) {
        this.methodName = methodName;
        this.traceID = traceID;
        this.eoi = eoi;
        this.ess = ess;
    }

    @Override
    public String toString() {
        return methodName + ";" + traceID + ";" + eoi + ";" + ess;
    }
}