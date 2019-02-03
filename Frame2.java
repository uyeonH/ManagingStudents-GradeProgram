//20171064 황유연, 20171040 최세영
import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import java.awt.event.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Frame2 extends JFrame {
	// 패널들과 Frame간의 연결을 위한 패널 클래스 생성
	// 모두 같은 패키지 내부에 존재해야 함
	EnterGrade enter = new EnterGrade();
	RankTable rankTable = new RankTable();
	IndividualGradePanel individualPanel = new IndividualGradePanel();
	ChartByLecture chartByLecture;
	OpenActionListener oal = new OpenActionListener();
	String fileName;
	setFile setF;
	JTabbedPane pane = new JTabbedPane();

	int init = 0;
	//프레임 생성자
	public Frame2() {
		super("성적 프로그램");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		JTabbedPane pane = createTabbedPane();
		c.add(pane, BorderLayout.CENTER);
		createMenu();
		setSize(800, 800);
		setVisible(true);
	}

	// 탭
	private JTabbedPane createTabbedPane() {

		if (init == 0) {
			setF = new setFile("C:\\Users\\User\\Documents\\info.txt");
			// 학생들의 기본정보 불러오기 
			rankTable = new RankTable(setF.setArray());
			individualPanel = new IndividualGradePanel(setF.setArray());
			chartByLecture = new ChartByLecture(setF.setArray());
			init++;
			// 처음 프로그램 실행하자마자 기본 파일 열리고,
			// 그 뒤에 새로운 텍스트파일을 중첩해서 열 경우에는 이 기본파일이 또 열리지 않고
			// 새로운 파일이 열려서 덮을 수 있도록 init 변수 사용
		}
		pane.addTab("성적 입력", enter);
		pane.addTab("전체 성적 조회", rankTable);
		pane.addTab("개인 성적 조회", individualPanel);
		pane.addTab("강의별 세부 조회", chartByLecture);
		return pane;
	}

	// 메뉴바
	private void createMenu() {
		//메뉴바에서 File->Open을 누르면 액션리스너를 통해 파일을 불러온다.
		JMenu fileMenu = new JMenu("File");
		JMenuItem openItem = new JMenuItem("Open");
		openItem.addActionListener(oal);
		fileMenu.add(openItem);
		JMenu editMenu = new JMenu("Edit");
		JMenu viewMenu = new JMenu("View");
		JMenu inputMenu = new JMenu("Help");
		JMenuBar mb = new JMenuBar();
		mb.add(fileMenu);
		mb.add(editMenu);
		mb.add(viewMenu);
		mb.add(inputMenu);
		setJMenuBar(mb);

	}

	// 파일 다이얼로그 띄우기
	class OpenActionListener implements ActionListener {
		private JFileChooser chooser;

		public OpenActionListener() {
			chooser = new JFileChooser();
		}

		public void actionPerformed(ActionEvent e) {
			FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT Files Only", "txt");
			chooser.setFileFilter(filter);
			int ret = chooser.showOpenDialog(null);
			if (ret != JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(null, "파일을 선택하지 않았습니다.", "경고", JOptionPane.WARNING_MESSAGE);
				return;
			}

			File file = chooser.getSelectedFile();

			// 불러온 파일 이름을 띄운다
			setF = new setFile(file.getPath());

			//각 클래스의 객체를 다시 생성해주고 탭패인을 다시 만든다.
			rankTable = new RankTable(setF.setArray());
			individualPanel = new IndividualGradePanel(setF.setArray());
			chartByLecture = new ChartByLecture(setF.setArray());
			pane.removeAll();
			pane = createTabbedPane();
			enter.fileNametf.setText(file.getName());
		}

	}

	/**********************************************************************************/
	// 성적 입력 판넬
	//상대평가시 A,B,B의 비율이 저장되는 변수
	int gradePercent[] = new int[3];
	//flag가 참일경우 상대평가, 거짓일 경우 절대평가
	boolean flag = false;

	class EnterGrade extends JPanel {

		JRadioButton absolute = new JRadioButton("절대평가");
		JRadioButton relative = new JRadioButton("상대평가");

		// ABC 학점 비율 텍스트필드
		JTextField abc[] = new JTextField[3];
		
		//파일이름 텍스트필드, 과목의 텍스트필드와 체크박스, 
		JTextField fileNametf = new JTextField(15);
		JTextField[] sub_tf = new JTextField[4];
		JCheckBox[] sub_check = new JCheckBox[4];

		/*************** 생성자 *****************/
		public EnterGrade() {

			setBackground(Color.white);
			// 판넬로 나누기
			JPanel[] p = new JPanel[11];
			// 과목 이름과 텍스트필드를 같이 붙인 판넬
			JPanel[] smallP = new JPanel[5];
			// Layout 설정
			
			setLayout(new BorderLayout());
			
			// panel 테두리
			for (int i = 0; i < p.length; i++) {
				p[i] = new JPanel();
				p[i].setBorder(BorderFactory.createLineBorder(Color.black));
			}

			/*************** p1 *****************/
			TitledBorder title;
			title = BorderFactory.createTitledBorder("파일 이름");
			p[1].setBorder(title);
			fileNametf.setEditable(false);
			p[1].add(fileNametf);

			/*************** p2 *****************/
			title = BorderFactory.createTitledBorder("평가 방법");
			p[2].setBorder(title);
			ButtonGroup group = new ButtonGroup();

			absolute.addActionListener(new Method());
			relative.addActionListener(new Method());

			group.add(absolute);
			group.add(relative);

			p[2].add(absolute, BorderLayout.EAST);
			p[2].add(relative, BorderLayout.EAST);

			/*************** p3 *****************/
			title = BorderFactory.createTitledBorder("학점 기준");
			p[3].setBorder(title);
			p[3].setLayout(new GridLayout(3, 1));
			JLabel ABC[] = { new JLabel(" A "), new JLabel(" B "), new JLabel(" C ") };

			JButton set = new JButton("설정");

			for (int i = 0; i < ABC.length; i++) {

				abc[i] = new JTextField(4);
				abc[i].setEnabled(false);
				p[3].add(ABC[i]);
				p[3].add(abc[i]);

			}

			/*************** p4 *****************/
			p[4].add(set);
			// 설정 버튼을 누르면 학점 비율이 멤버변수 gradePercent에 저장됨
			set.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					if (flag == false) {
						JOptionPane.showMessageDialog(null,
								"절대평가로 적용됩니다. 90점 이상 A, 80점 이상 90점 미만 B, 70점 이상 80점 미만 C, 그 이하 D", "성공",
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						for (int i = 0; i < abc.length; i++) {

							gradePercent[i] = 0;
							gradePercent[i] = Integer.parseInt(abc[i].getText());

						}

						JOptionPane
								.showMessageDialog(null,
										"A: " + gradePercent[0] + "% " + "B: " + gradePercent[1] + "% " + "C: "
												+ gradePercent[2] + "% 비율이 저장되었습니다.",
										"성공", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
			/*************** p5 *****************/
			title = BorderFactory.createTitledBorder("점수 입력");
			p[5].setBorder(title);

			p[5].setLayout(new GridLayout(5, 1));

			JLabel st_num = new JLabel("       학번");
			JLabel[] sub_la = new JLabel[4];

			JTextField num = new JTextField(10);

			String[] sub_name = { "   Java", "   소프트웨어 분석 설계", "   컴퓨터 구조", "   객체지향 프로그래밍" };

			JButton saveBtn = new JButton("테이블에 추가하기");
			JButton clearBtn = new JButton("입력 초기화");
			JButton allCheckBtn = new JButton("모두 체크");
			for (int i = 0; i < sub_name.length; i++) {
				sub_la[i] = new JLabel(sub_name[i]);
				sub_check[i] = new JCheckBox(sub_name[i], false);
				sub_tf[i] = new JTextField(10);

				sub_tf[i].setEnabled(false);

			}
			allCheckBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (int i = 0; i < sub_name.length; i++) {
						sub_check[i].setSelected(true);

						sub_tf[i].setEnabled(true);

					}
				}
			});
			clearBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == clearBtn) {
						num.setText("");

						for (int i = 0; i < sub_name.length; i++) {
							sub_check[i].setSelected(false);
							sub_tf[i].setText("");
							sub_tf[i].setEnabled(false);

						}

					}
				}
			}); // 초기화 버튼 클릭시 체크박스 해제, 내용 삭제

			p[5].add(st_num);
			p[5].add(num);

			for (int i = 0; i < sub_la.length; i++) {

				p[5].add(sub_check[i]);
				p[5].add(sub_tf[i]);
				sub_check[i].addItemListener(new checkListener());
			}

			/*************** p6 *****************/
			p[6].add(saveBtn);
			p[6].add(clearBtn);
			p[6].add(allCheckBtn);
			saveBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (int i = 0; i < sub_name.length; i++) {
						sub_tf[i].setEnabled(true);
					}

					Object inputStr[] = new Object[10];

					inputStr[2] = num.getText();

					for (int i = 0; i < 4; i++) {
						if (sub_tf[i].getText().equals(""))
							sub_tf[i].setText(" ");
					}

					inputStr[5] = sub_tf[0].getText();
					inputStr[6] = sub_tf[1].getText();
					inputStr[7] = sub_tf[2].getText();
					inputStr[8] = sub_tf[3].getText();

					int index = 0;
					int break_point = 0;
					for (int i = 0; i < rankTable.tablemodel.getRowCount(); i++) {

						if (rankTable.rowData[i][2].equals(num.getText())) {
							index = i;
							break_point++;
							break;
						}
					}
					// 학번에 해당하는 학생이 없을 경우
					if (break_point == 0) {
						JOptionPane.showMessageDialog(null, "학번이 맞는 학생이 없습니다.", "경고", JOptionPane.WARNING_MESSAGE);
						num.setText("");

						sub_tf[0].setText("");
						sub_tf[1].setText("");
						sub_tf[2].setText("");
						sub_tf[3].setText("");

						return;
					}

					rankTable.rowData[index][5] = inputStr[5];
					rankTable.rowData[index][6] = inputStr[6];
					rankTable.rowData[index][7] = inputStr[7];
					rankTable.rowData[index][8] = inputStr[8];

					for (int i = 5; i < 9; i++) {
						if (!(rankTable.tablemodel.getValueAt(index, i).equals(" "))) {
							System.out.println("이미 기재된 성적입니다.");

							inputStr[i] = rankTable.tablemodel.getValueAt(index, i);
						}
					}

					inputStr[1] = rankTable.tablemodel.getValueAt(index, 1);
					inputStr[3] = rankTable.tablemodel.getValueAt(index, 3);
					inputStr[4] = rankTable.tablemodel.getValueAt(index, 4);

					rankTable.tablemodel.removeRow(index);
					rankTable.tablemodel.insertRow(index, inputStr);

					rankTable.setAverageColumn(rankTable.rowData);

					for (int i = 0; i < 3; i++) {
						pane.removeTabAt(1);
					}

					pane.addTab("전체 성적 조회", rankTable);
					pane.addTab("개인 성적 조회", individualPanel);
					pane.addTab("강의별 세부 조회", chartByLecture);

					JOptionPane.showMessageDialog(null, num.getText() + " " + inputStr[3] + "의 성적이 추가되었습니다.", "성공",
							JOptionPane.INFORMATION_MESSAGE);

					num.setText("");

					sub_tf[0].setText("");
					sub_tf[1].setText("");
					sub_tf[2].setText("");
					sub_tf[3].setText("");

				}
			});

			p[0].add(p[1]);
			p[0].add(p[2]);
			p[0].add(p[3]);
			p[0].add(p[4]);
			add(p[0], BorderLayout.NORTH);
			add(p[5], BorderLayout.CENTER);
			add(p[6], BorderLayout.SOUTH);

		}
		//체크박스를 선택/해제했을 때 텍스트필드의 enable 정하기
		class checkListener implements ItemListener {
			@Override
			public void itemStateChanged(ItemEvent e) {

				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (e.getItem() == sub_check[0])
						sub_tf[0].setEnabled(true);
					else if (e.getItem() == sub_check[1])
						sub_tf[1].setEnabled(true);
					else if (e.getItem() == sub_check[2])
						sub_tf[2].setEnabled(true);
					else if (e.getItem() == sub_check[3])
						sub_tf[3].setEnabled(true);
				} else {
					if (e.getItem() == sub_check[0])
						sub_tf[0].setEnabled(false);
					else if (e.getItem() == sub_check[1])
						sub_tf[1].setEnabled(false);
					else if (e.getItem() == sub_check[2])
						sub_tf[2].setEnabled(false);
					else if (e.getItem() == sub_check[3])
						sub_tf[3].setEnabled(false);
				}
			}
		}

		// 상대평가 버튼 클릭시 학점 비율 입력 활성화
		class Method implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == absolute) {
					flag = false;
					for (int i = 0; i < 3; i++) {
						abc[i].setEnabled(flag);
						
					}
				} else {
					flag = true;
					for (int i = 0; i < 3; i++) {
						abc[i].setEnabled(flag);
						
					}}
			}
		}
	}

	/**********************************************************************************/
	// 전체 성적 조회 판넬
	class RankTable extends JPanel {

		JPanel p1 = new JPanel();
		JPanel p2 = new JPanel();
		JPanel p3 = new JPanel();
		JPanel p4 = new JPanel();
		JPanel p5 = new JPanel();

		JLabel[] lab = new JLabel[2];
		//파일 저장버튼
		JButton store_btn = new JButton("파일 저장하기");
		//열의 이름
		String columnNames[] = { "석차", "학과", "학번", "이름", "전화번호", "Java", "소분설", "컴구", "객프", "평균" };
		//테이블에 들어갈 2차원 배열
		Object rowData[][];
		//테이블 만들기
		DefaultTableModel tablemodel = new DefaultTableModel(rowData, columnNames);
		JTable jtable = new JTable(tablemodel);
		JScrollPane scroll;
		// 퀵소트 생성자
		QuickSort quick = new QuickSort();
		/***************생성자****************/
		public RankTable() {

			rowData = new Object[20][10];
			jtable.setEnabled(false); // 수정 불가
			jtable.getColumn("전화번호").setPreferredWidth(200);
			jtable.getColumn("학번").setPreferredWidth(130);
			jtable.getColumn("학과").setPreferredWidth(130);
			jtable.getColumn("이름").setPreferredWidth(100);
			scroll = new JScrollPane(jtable);
			scroll.setPreferredSize(new Dimension(690, 330));

			this.setBackground(Color.white);
			setLayout(new BorderLayout());

			p1.add(scroll);

			p2.add(store_btn);
			add(p3, BorderLayout.NORTH);
			add(p1, BorderLayout.CENTER);
			add(p2, BorderLayout.SOUTH);
			
		}
		/***************생성자****************/
		public RankTable(Object[][] row) { //2차원 배열을 인수로 하는 생성자
			//이전의 테이블을 삭제하고 다시 만든다.
			removeAll();

			rowData = new Object[row.length][10];

			for (int i = 0; i < row.length; i++) {
				for (int j = 0; j < 8; j++) {
					rowData[i][j + 1] = row[i][j];
				}
			}

			this.setBackground(Color.white);
			setLayout(new BorderLayout());

			// p[0] 사용 안함

			tablemodel = new DefaultTableModel(rowData, columnNames);
			setAverageColumn(rowData);

			jtable = new JTable(tablemodel);
			jtable.setAutoCreateRowSorter(true);
			//TableRowSorter tablesorter = new TableRowSorter(jtable.getModel());
			//jtable.setRowSorter(tablesorter);
			jtable.setEnabled(false); // 수정 불가
			jtable.getColumn("전화번호").setPreferredWidth(200);
			jtable.getColumn("학번").setPreferredWidth(130);
			jtable.getColumn("학과").setPreferredWidth(130);
			jtable.getColumn("이름").setPreferredWidth(100);
			scroll = new JScrollPane(jtable);

			// DefaultTableCellHeaderRenderer 생성 (가운데 정렬)

			DefaultTableCellRenderer tScheduleCellRenderer = new DefaultTableCellRenderer();
			tScheduleCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
			TableColumnModel tcmSchedule = jtable.getColumnModel();
			for (int i = 0; i < tcmSchedule.getColumnCount(); i++) {
				tcmSchedule.getColumn(i).setCellRenderer(tScheduleCellRenderer);
			}

			scroll.setPreferredSize(new Dimension(690, 330));

			p1.add(scroll);
			p2.add(store_btn);
			p4.add(new Label("Click Column to Sort"));
			p5.setLayout(new BoxLayout(p5, BoxLayout.Y_AXIS));
			p5.add(p2);
			p5.add(p4);
			add(p3, BorderLayout.NORTH);
			add(p1, BorderLayout.CENTER);
			add(p5, BorderLayout.SOUTH);

			repaint(); 

			// 파일에 저장하기, store_btn의 액션리스너
			store_btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					FileOut FO = new FileOut(rankTable.tablemodel);
				}
			});

		}

		/**** 평균 가져와서 열에 추가하기 ****/
		public void setAverageColumn(Object[][] rowData) {
			float[] avgColumn = new float[tablemodel.getRowCount()];
			float sum = 0;
			int j = 0;
			int count = 0;// 과목 갯수
			int full_aver = 0;
			int place;
			int empty;
			if (init == 0) {
				++init;
				return;
			} // 초기 화면 불러올 경우 등수 건드리지 않음

			for (int i = 0; i < tablemodel.getRowCount(); i++) {
				sum = 0;
				count = 0;
				full_aver = 0;

				empty = 0;
				for (j = 5; j < 9; j++) {
					if (!(tablemodel.getValueAt(i, j).equals(" "))) {
						String change = rowData[i][j] + "";
						sum += (Float.parseFloat(change));
						count++;
					} else if (tablemodel.getValueAt(i, j).equals(" ")) {
						++empty;
					}
				}

				if (empty == 4) {
					tablemodel.setValueAt(0.0, i, 9);
					avgColumn[i] = 0;
				} else {
					tablemodel.setValueAt(sum / count, i, 9); // 전체 4과목의 평균을 계산해서 저장
					float temp = sum / count;
					avgColumn[i] = temp;
				}

			}

			quick.sort(avgColumn, 0, avgColumn.length - 1);
			// System.out.println(avgColumn.length);
			/*for (int i = 0; i < avgColumn.length; i++) {
				System.out.println(avgColumn[i]);
			}*/
			place = 1;
			int repeat = 0;
			for (int j1 = avgColumn.length - 1; j1 >= 0; j1--) {
				repeat = 0;
				String s1 = avgColumn[j1] + "";
				float compare_num = (Float.parseFloat(s1));
				if (j1 <= avgColumn.length - 2) {
					if (avgColumn[j1] == avgColumn[j1 + 1]) {
						continue;
					}
				}
				for (int k = 0; k < avgColumn.length; k++) {
					String s = tablemodel.getValueAt(k, 9) + "";
					float temp_num = (Float.parseFloat(s));

					if (compare_num == temp_num) {
						tablemodel.setValueAt(place, k, 0);
						rowData[k][0] = place;
						repeat++;
						//System.out.println("repeat" + repeat);
						//System.out.println("place" + place);
					}

				}

				place = place + repeat;

			}

		}
	}

	/**********************************************************************************/
	// 개인 성적 조회 판넬
	class IndividualGradePanel extends JPanel {
		Grade g;
		Object table[][];
		String[] majorName = { "computer", "math", "stat", "info" };
		String[] stNum = { "14", "15", "16", "17" };

		JTextField nameTf = new JTextField(7);
		JComboBox<String> Major;
		JComboBox<String> StudentID;
		JLabel[] la = new JLabel[4];
		// 하단 라벨
		JLabel bottom_la = new JLabel("학과, 학번, 이름을 입력하고 조회 버튼을 눌러주세요.");
		// p[0] 상단의 바, p[1] 그래프 출력, p[2] 하단 라벨
		JPanel[] p = new JPanel[3];
		//열두개로 나누어진 판넬
		JPanel[] sg = new JPanel[12];
		JButton viewButton = new JButton("조회");
		// 기존 데이터 표 가져오기
		DefaultTableModel targetModel;
		JLabel newOne;
		//막대그래프를 그리는 판넬
		DrawingPanel dp1, dp2, dp3, dp4;

		public IndividualGradePanel() {

			table = new Object[20][10];

			this.setBackground(Color.white);
			setLayout(new BorderLayout());

			la[0] = new JLabel("학과");
			la[1] = new JLabel("학번");
			la[2] = new JLabel("이름");
			
			// 판넬 생성
			for (int i = 0; i < p.length; i++) {
				p[i] = new JPanel();
			}

			// 학과 선택 콤보박스 생성
			Major = new JComboBox<String>();
			for (int i = 0; i < majorName.length; i++)
				Major.addItem(majorName[i]);

			// 학번 선택 콤보박스 생성
			StudentID = new JComboBox<String>();
			for (int i = 0; i < stNum.length; i++)
				StudentID.addItem(stNum[i]);
			
			//"조회"버튼에 액션리스너 추가
			viewButton.addActionListener(new findInfo());

			newOne = new JLabel("학점 기준을 선택하지 않고 진행하는 경우, 학생의 등급이 절대평가로 정해지므로 변경을 원할 시 재설정을 해주세요.");

			// 판넬에 부착하기
			p[0].add(la[0]);
			p[0].add(Major);
			p[0].add(la[1]);
			p[0].add(StudentID);
			p[0].add(la[2]);
			p[0].add(nameTf);
			p[0].add(viewButton);

			p[2].add(bottom_la);
			add(p[0], BorderLayout.NORTH);
			add(p[2], BorderLayout.SOUTH);

		}
		
		/***************생성자****************/
		// 위 생성자와 내용이 거의 동일해 주석을 생략합니다.
		public IndividualGradePanel(Object[][] row) { //인자가 2차원 배열인 생성자

			table = new Object[row.length][10];

			for (int i = 0; i < row.length; i++) {
				for (int j = 0; j < 8; j++) {
					table[i][j + 1] = row[i][j];
				}
			}

			this.setBackground(Color.white);
			setLayout(new BorderLayout());

			la[0] = new JLabel("학과");
			la[1] = new JLabel("학번");
			la[2] = new JLabel("이름");
			
			for (int i = 0; i < p.length; i++) {
				p[i] = new JPanel();
			}
			Major = new JComboBox<String>();
			for (int i = 0; i < majorName.length; i++)
				Major.addItem(majorName[i]);

			StudentID = new JComboBox<String>();
			for (int i = 0; i < stNum.length; i++)
				StudentID.addItem(stNum[i]);

			viewButton.addActionListener(new findInfo());

			newOne = new JLabel("학점 기준을 선택하지 않고 진행하는 경우, 학생의 등급이 절대평가로 정해지므로 변경을 원할 시 재설정을 해주세요.");

			p[0].add(la[0]);
			p[0].add(Major);
			p[0].add(la[1]);
			p[0].add(StudentID);
			p[0].add(la[2]);
			p[0].add(nameTf);
			p[0].add(viewButton);

			JPanel p0_1 = new JPanel();
			p0_1.add(newOne);

			JPanel p_array = new JPanel();
			p_array.setLayout(new BoxLayout(p_array, BoxLayout.Y_AXIS));
			p_array.add(p[0]);
			p_array.add(p0_1);

			p[2].add(bottom_la);
			add(p_array, BorderLayout.NORTH);
			add(p[1], BorderLayout.CENTER);
			add(p[2], BorderLayout.SOUTH);

		}

		// 학과, 학번, 이름까지 찾아서 테이블 출력
		class findInfo implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				//구성요소 모두 지우기
				p[1].removeAll();
				Object[][] find = new Object[rankTable.rowData.length][10];
				// 테이블에 빈자리가 있으면, 모두 채우라는 경고메세지 
				for (int i = 0; i < rankTable.rowData.length; i++) {
					if (rankTable.tablemodel.getValueAt(i, 5).equals(" ")
							|| rankTable.tablemodel.getValueAt(i, 6).equals(" ")
							|| rankTable.tablemodel.getValueAt(i, 7).equals(" ")
							|| rankTable.tablemodel.getValueAt(i, 8).equals(" ")) {
						JOptionPane.showMessageDialog(null, "성적을 모두 채워주세요.", "경고", JOptionPane.WARNING_MESSAGE);
						return;
					}
				}
				//다시 테이블 생성하기
				Object[] columnNames = { "석차", "학과", "학번", "이름", "전화번호", "Java", "소분설", "컴구", "객프", "평점" };
				DefaultTableModel find_model = new DefaultTableModel(find, columnNames);
				
				//콤보박스 선택시 값 저장하기
				int index1 = Major.getSelectedIndex();
				String target1 = majorName[index1];

				int index2 = StudentID.getSelectedIndex();
				String num_target = stNum[index2];

				String target2 = nameTf.getText();

				String year_num;
				int st_index = 0;
				int addNum = 0;
				//상대평가일 때
				if (flag == true) {
					//텍스트필드가 모두 0이면 경고메세지
					if (gradePercent[0] == 0 && gradePercent[1] == 0 && gradePercent[2] == 0) {
						JOptionPane.showMessageDialog(null, "학점 기준을 먼저 설정해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
						return;
					}
				}
				//테이블의 학번, 학과와 비교하여 하당하는 학생 배열의 index 찾기
				for (int i = 0; i < rankTable.rowData.length; i++) {
					year_num = ((String) rankTable.rowData[i][2]);
					year_num = year_num.substring(2, 4);
					//System.out.println("year_num="+year_num);
					if ((rankTable.rowData[i][1].equals(target1)) && (year_num.equals(num_target))
							&& (rankTable.rowData[i][3].equals(target2))) {
						find_model.addRow(rankTable.rowData[i]);
						st_index = i;
						addNum++;
						//System.out.println("rankTable.rowData[i][1]: "+rankTable.rowData[i][1]+", target1: "+(target1));
						//System.out.println("year_num: "+year_num+"target1: "+num_target);
						//System.out.println("rankTable.rowData[i][3]: "+rankTable.rowData[i][3]+", target2"+(target2));
						
					}
				}
				//학생을 못찾았으면 경고메세지
				if (addNum == 0) {
					JOptionPane.showMessageDialog(null, "조건에 해당하는 학생이 없습니다.", "경고", JOptionPane.WARNING_MESSAGE);
				} else {
					// 레이블의 내용: 학생의 정보에 맞게 바꾸기
					String c = rankTable.rowData[st_index][1] + " " + rankTable.rowData[st_index][2] + " " + target2
							+ "의 성적입니다.";
					newOne.setText(c);

					g = new Grade(rankTable.tablemodel);
					
					//12개의 판넬 gridLayout으로 구성
					p[1].setLayout(new GridLayout(4, 3));
					p[1].setBorder(BorderFactory.createLineBorder(Color.black));
					//판넬 생성, 부착
					for (int i = 0; i < sg.length; i++) {
						sg[i] = new JPanel();
						sg[i].setBorder(BorderFactory.createLineBorder(Color.white));
						p[1].add(sg[i]);
					}
					
					if (flag == false)
						g.setAbsoluteGrade(st_index);
					else if (flag == true)
						g.setGrade(st_index);
					
					//DrawingPanel의 객체를 과목마다 생성
					dp1 = new DrawingPanel();
					sg[1].setLayout(new BoxLayout(sg[1], BoxLayout.X_AXIS));
					sg[1].add(dp1);
					sg[1].setOpaque(true);
					sg[1].setVisible(true);
					
					dp2 = new DrawingPanel();
					sg[4].setLayout(new BoxLayout(sg[4], BoxLayout.X_AXIS));
					sg[4].add(dp2);
					sg[4].setOpaque(true);
					sg[4].setVisible(true);
					
					dp3 = new DrawingPanel();
					sg[7].setLayout(new BoxLayout(sg[7], BoxLayout.X_AXIS));
					sg[7].add(dp3);
					sg[7].setOpaque(true);
					sg[7].setVisible(true);
					
					dp4 = new DrawingPanel();
					sg[10].setLayout(new BoxLayout(sg[10], BoxLayout.X_AXIS));
					sg[10].add(dp4);
					sg[10].setOpaque(true);
					sg[10].setVisible(true);
					String s;
					
					//(1,1)위치의 판넬
					sg[0].setLayout(new GridLayout(1, 1));
					sg[0].add(new JLabel("JAVA", SwingConstants.CENTER));
					
					//(1,3)위치의 판넬, 평균, 최고점, 최하점, 중간값 등을 테이블에서 가져와 라벨에 출력
					sg[2].setLayout(new BoxLayout(sg[2], BoxLayout.Y_AXIS));
					sg[2].add((new JLabel("       전체 평균:" + g.getSub1Aver())));
					sg[2].add(new JLabel("       최고점:" + Integer.toString(g.getHigh1())));
					sg[2].add(new JLabel("       최하점:" + Integer.toString(g.getLow1())));
					sg[2].add(new JLabel("       중간값:" + Integer.toString(g.getMid1())));
					
					//문자변수에 저장 후 정수형으로 형변환
					s = rankTable.tablemodel.getValueAt(st_index, 5) + "";
					int num = Integer.parseInt(s);
					sg[2].add(new JLabel("       학생의 점수:" + num));
					//Grade의 객체 g에서 평균을 형변환시키고 반올림 해준 후 학생의 점수, 최하점, 최고점으로 drawingPanel의 메소드 호출
					String t1 = g.getSub1Aver() + "";
					Float t2 = Float.parseFloat(t1);
					int t3 = (Math.round(t2));
					dp1.setValue(num, g.getLow1(), t3, g.getHigh1());

					g.setRank1(num);
					sg[2].add(new JLabel("       학점:" + g.getGrade1()));
					sg[2].add(new JLabel("       등수:" + Integer.toString(g.getRank1()) + "/" + Integer.toString(g.getTotal1()),
							SwingConstants.CENTER));

					sg[3].setLayout(new GridLayout(1, 1));
					sg[3].add(new JLabel("소프트웨어분석설계", SwingConstants.CENTER));

					sg[5].setLayout(new BoxLayout(sg[5], BoxLayout.Y_AXIS));
					sg[5].add(new JLabel("       전체 평균:" + g.getSub2Aver()));
					sg[5].add(new JLabel("       최고점:" + Integer.toString(g.getHigh2())));
					sg[5].add(new JLabel("       최하점:" + Integer.toString(g.getLow2())));
					sg[5].add(new JLabel("       중간값:" + Integer.toString(g.getMid2())));

					s = rankTable.tablemodel.getValueAt(st_index, 6) + "";
					num = Integer.parseInt(s);
					sg[5].add(new JLabel("       학생의 점수:" + num));

					t1 = g.getSub2Aver() + "";
					t2 = Float.parseFloat(t1);
					t3 = (Math.round(t2));
					dp2.setValue(num, g.getLow2(), t3, g.getHigh2());
					g.setRank2(num);
					sg[5].add(new JLabel("       학점:" + g.getGrade2()));
					sg[5].add(new JLabel("       등수:" + Integer.toString(g.getRank2()) + "/" + Integer.toString(g.getTotal2()),
							SwingConstants.CENTER));

					sg[6].setLayout(new GridLayout(1, 1));
					sg[6].add(new JLabel("   컴퓨터구조", SwingConstants.CENTER));

					sg[8].setLayout(new BoxLayout(sg[8], BoxLayout.Y_AXIS));
					sg[8].add(new JLabel("       전체 평균:" + g.getSub3Aver()));
					sg[8].add(new JLabel("       최고점:" + Integer.toString(g.getHigh3())));
					sg[8].add(new JLabel("       최하점:" + Integer.toString(g.getLow3())));
					sg[8].add(new JLabel("       중간값:" + Integer.toString(g.getMid3())));

					s = rankTable.tablemodel.getValueAt(st_index, 7) + "";
					num = Integer.parseInt(s);
					sg[8].add(new JLabel("       학생의 점수:" + num));
					t1 = g.getSub3Aver() + "";
					t2 = Float.parseFloat(t1);
					t3 = (Math.round(t2));
					dp3.setValue(num, g.getLow3(), t3, g.getHigh3());
					g.setRank3(num);
					sg[8].add(new JLabel("       학점:" + g.getGrade3()));
					sg[8].add(new JLabel("       등수:" + Integer.toString(g.getRank3()) + "/" + Integer.toString(g.getTotal3()),
							SwingConstants.CENTER));

					sg[9].setLayout(new GridLayout(1, 1));
					sg[9].add(new JLabel("객체지향 프로그래밍", SwingConstants.CENTER));

					sg[11].setLayout(new BoxLayout(sg[11], BoxLayout.Y_AXIS));
					sg[11].add(new JLabel("       전체 평균:" + g.getSub4Aver()));
					sg[11].add(new JLabel("       최고점:" + Integer.toString(g.getHigh4())));
					sg[11].add(new JLabel("       최하점:" + Integer.toString(g.getLow4())));
					sg[11].add(new JLabel("       중간값:" + Integer.toString(g.getMid4())));
					s = rankTable.tablemodel.getValueAt(st_index, 8) + "";
					num = Integer.parseInt(s);

					sg[11].add(new JLabel("       학생의 점수:" + num));
					t1 = g.getSub4Aver() + "";
					t2 = Float.parseFloat(t1);
					t3 = (Math.round(t2));
					dp4.setValue(num, g.getLow4(), t3, g.getHigh4());
					g.setRank4(num);
					sg[11].add(new JLabel("       학점:" + g.getGrade4()));
					sg[11].add(
							new JLabel("       등수:" + Integer.toString(g.getRank4()) + "/" + Integer.toString(g.getTotal4())));

					dp1.repaint();
					dp2.repaint();
					dp3.repaint();
					dp4.repaint();
					
					//학생 성적 입력 판넬에서 저장했던 A,B,C의 학점 비율을 저장하는 변수
					int A = (int) (gradePercent[0] * 0.01 * rankTable.tablemodel.getRowCount());
					int B = (int) (gradePercent[1] * 0.01 * rankTable.tablemodel.getRowCount());
					int C = (int) (gradePercent[2] * 0.01 * rankTable.tablemodel.getRowCount());

					int total_place = Integer.parseInt(rankTable.tablemodel.getValueAt(st_index, 0) + "");
					//System.out.println("Rank1: " + total_place);
					String total_rank;
					
					if(flag==true) {
						if (A >= total_place)
							total_rank = "A";
						else if (A + B >= total_place)
							total_rank = "B";
						else if (A + B + C >= total_place)
							total_rank = "C";
						else
							total_rank = "D";
					}else {
						String ch_tmp=rankTable.tablemodel.getValueAt(st_index, 9)+"";
						float chnum=(Float.parseFloat(ch_tmp));
						int ch_num=(Math.round(chnum));
								
						if (ch_num >= 90)
							total_rank = "A";
						else if (ch_num < 90 && ch_num >= 80)
							total_rank = "B";
						else if (ch_num< 80 && ch_num >= 70)
							total_rank= "C";
						else
							total_rank = "D";

					}
					//하단부에 평균 학점, 총 평점의 석차를 보여주는 라벨
					bottom_la.setText("평균 학점은 " + total_rank + " 이며 석차는 " + rankTable.tablemodel.getValueAt(st_index, 0)
							+ "등 입니다.");

				}
			}

		}
		
	}
   /************************************************************************************/
	//막대그래프 그리는 판넬
	class DrawingPanel extends JPanel { 

		int score = 0;
		int lowest = 0;
		int average = 0;
		int highest = 0;

public void paint(Graphics g) {
			
			g.clearRect(0, 0, getWidth(), getHeight());
			g.drawLine(50, 130, 240, 130);
			//가로선과 y축의 값을 입력
			for (int cnt = 1; cnt < 11; cnt++) {
				if (cnt % 2 == 0) {
					g.drawString(cnt * 10 + "", 25, 135 - 10 * cnt);
					g.drawLine(50, 130 - 10 * cnt, 240, 130 - 10 * cnt);
				}
			}
			g.drawLine(50, 20, 50, 130); // y축
			g.drawString("점수", 75, 145);
			g.drawString("최하점", 115, 145);
			g.drawString("평균", 160, 145);
			g.drawString("최고점", 195, 145);
			//색을 지정하고 막대 그리기
			g.setColor(Color.GREEN);
			g.fillRect(80, 130 - score, 20, score);
			g.setColor(Color.black);
			g.fillRect(120, 130 - lowest, 20, lowest);
			g.setColor(Color.blue);
			g.fillRect(160, 130 - average, 20, average);
			g.setColor(Color.red);
			g.fillRect(200, 130 - highest, 20, highest);
		}
		
		
		//외부에서 값을 받아서 저장하는 메소드
		void setValue(int score, int lowest, int average, int highest) {
			this.score = score;
			this.lowest = lowest;
			this.average = average;
			this.highest = highest;

			repaint(); //다시그리기
		}

	}

	/**********************************************************************************/
	// 강의별 세부조회 판넬
	class ChartByLecture extends JPanel {
		//과목별 기준에 따른 학생수 저장, 파이차트에 사용됨
		int[] gradeData = { 0, 0, 0, 0 };
		int[] scoreData = { 0, 0, 0, 0 };
		int[] majorData = { 0, 0, 0, 0 };
		//각을 저장할 배열
		int[] arcAngle = new int[4];
		int[] arcAngle2 = new int[4];
		int[] arcAngle3 = new int[4];
		private Color[] color = { Color.RED, Color.BLUE, Color.green, Color.ORANGE };
		//라벨에 출력한 문자열들
		private String[] itemName = { "14", "15", "16", "17" };
		private String[] itemName2 = { "under 60", "70", "80", "90" };
		private String[] itemName3 = { "Com&IT", "Math", "Stat", "Info" };
		Font f = new Font("함초롬돋움", Font.PLAIN, 15);
		JLabel[] la = { new JLabel("강의명   "), // 0
				new JLabel("Rate by ID"), // 1
				new JLabel("Rate by Score"), // 2
				new JLabel("Rate by Major"), // 3
				new JLabel("라벨 글씨 크기 조절")// 4
		};

		// label 글씨크기 조절 슬라이더
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 10, 20, 15);
		JComboBox cb = new JComboBox();
		String[] majorName = { "Java", "소분설", "컴퓨터 구조", "객프" };
		int size;
		Object table[][];
		JPanel p0 = new JPanel();
		JPanel p1 = new JPanel();
		JPanel p2 = new JPanel();

		ChartPanel chartPanel;

		public ChartByLecture(Object[][] row) {

			table = new Object[row.length][10];

			for (int i = 0; i < row.length; i++) {
				for (int j = 0; j < 8; j++) {
					table[i][j + 1] = row[i][j];
				}
			}

			setLayout(new BorderLayout());
			setFont(f);

			// 판넬 생성

			/********* p[0] *********/
			// 판넬에 다른 요소들 붙이기
			for (int i = 0; i < majorName.length; i++)
				cb.addItem(majorName[i]);

			p0.add(la[0]);
			p0.add(cb);
			//강의명 콤보박스에 액션리스너 부착, 성적이 모두 채워져있지 않으면 경고를 띄움
			cb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					for (int i = 0; i < rankTable.rowData.length; i++) {
						if (rankTable.tablemodel.getValueAt(i, 5).equals(" ")
								|| rankTable.tablemodel.getValueAt(i, 6).equals(" ")
								|| rankTable.tablemodel.getValueAt(i, 7).equals(" ")
								|| rankTable.tablemodel.getValueAt(i, 8).equals(" ")) {
							JOptionPane.showMessageDialog(null, "성적을 모두 채워주세요.", "경고", JOptionPane.WARNING_MESSAGE);
							return;
						}
					}

					int index = cb.getSelectedIndex();
					drawChart(rankTable.tablemodel, index + 5,chartPanel);
					
				}

			});
			/********* p[1] *********/
			// 차트 출력
			chartPanel = new ChartPanel();
			p1.setLayout(new FlowLayout());
			p1.add(chartPanel);
			p1.setOpaque(true);
			p1.setVisible(true);

			p2.add(la[4]);
			p2.add(slider);

			// 판넬 부착
			add(p0, BorderLayout.NORTH);
			add(chartPanel, BorderLayout.CENTER);
			add(p2, BorderLayout.SOUTH);

			// slider 리스너 -> 폰트 조절
			slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					size = slider.getValue();
					chartPanel.setFont(new Font("돋움체",Font.PLAIN,size));
					
					for (int i = 0; i < la.length; i++) {
						chartPanel.repaint();
					}
				}
			});
		}

		//테이블과 인덱스를 인자로 주고 이 함수를 부르면 파이차트가 그려짐
		private void drawChart(DefaultTableModel table, int index,ChartPanel chartPanel) {
			int[] sum = { 0, 0, 0 };
			String year_num, score_num, major;
			this.chartPanel=chartPanel;
			for (int i = 0; i < gradeData.length; i++) {
				gradeData[i] = 0;
				scoreData[i] = 0;
				majorData[i] = 0;

				arcAngle[i] = 0;
				arcAngle2[i] = 0;
				arcAngle3[i] = 0;
			} // 초기화

			//전체학번중 두 숫자를 추출하여 비교 (예를들어 20171000 학번이면 17 추출)
			for (int j = 0; j < table.getRowCount(); j++) {
				year_num = (table.getValueAt(j, 2)) + "";
				year_num = year_num.substring(2, 4);
				//학번에 따라 학생 수 세기
				switch (year_num) {
				case "14":
					++gradeData[0];
					break;
				case "15":
					++gradeData[1];
					break;
				case "16":
					++this.gradeData[2];
					break;
				case "17":
					++gradeData[3];
					break;
				default:
					break;
				}

			}

			for (int j = 0; j < table.getRowCount(); j++) {
				score_num = (table.getValueAt(j, index)) + "";
				
				//System.out.println("score_num="+score_num+"");
				if (score_num.length() == 2) {
					score_num = score_num.substring(0, 1);
					//System.out.println("score_num.substring(0, 1)"+score_num+"");
				} else if (score_num.length() == 3) {
					score_num = score_num.substring(0, 2);
					//System.out.println("score_num.substring(0, 2)="+score_num+"");
				}
				
				//점수에 따라 학생 수 세기
				switch (score_num) {
				case "0":
					
				case "1":
					
				case "2":
					
				case "3":
					
				case "4":
					
				case "5":
				
				case "6":
					++scoreData[0];
					break;
				case "7":
					++scoreData[1];
					break;
				case "8":
					++scoreData[2];
					break;
				case "9":
					++scoreData[3];
					break;
				case "10":
					++scoreData[3];
				default:
					break;
				}

			}

			for (int j = 0; j < table.getRowCount(); j++) {
				major = (table.getValueAt(j, 1)) + "";
				//전공에 따라 학생 수 세기
				switch (major) {
				case "IT":
				case "computer":
					++majorData[0];
					break;
				case "math":
					++majorData[1];
					break;
				case "stat":
					++majorData[2];
					break;
				case "info":
					++majorData[3];
					break;
				default:
					break;
				}

			}

			//각 데이터의 해당 기준의 합계
			for (int i = 0; i < gradeData.length; i++) {
				sum[0] += gradeData[i];
				sum[1] += scoreData[i];
				sum[2] += majorData[i];

			}
			//각 기준마다(학번, 점수, 전공) 전체 중 얼마나 차지하는지 360도에서 차지하는 비율 계산
			for (int i = 0; i < gradeData.length; i++) {
				arcAngle[i] = (int) Math.round((double) gradeData[i] / (double) sum[0] * 360);
				arcAngle2[i] = (int) Math.round((double) scoreData[i] / (double) sum[1] * 360);
				arcAngle3[i] = (int) Math.round((double) majorData[i] / (double) sum[2] * 360);
			}

			

		

			chartPanel.repaint(); //다시그리기
			
		}

		class ChartPanel extends JComponent {
			
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				int startAngle = 0;
				int startAngle2 = 0;
				int startAngle3 = 0;

				g.setFont(new Font("돋움체", Font.PLAIN, 15 + size / 2));

				g.drawString(la[1].getText(), 130, 40);
				g.drawString(la[2].getText(), 330, 40);
				g.drawString(la[3].getText(),530, 40);

				// 라벨 출력
				for (int i = 0; i < 4; i++) {
					g.setColor(color[i]);
					//System.out.println("arcAngle[i]: "+arcAngle[i]+", Math.round(arcAngle[i] * 100. / 360.): "+Math.round(arcAngle[i] * 100. / 360.));
					g.drawString(itemName[i] + "학번: " + Math.round(arcAngle[i] * 100. / 360.) + "%", 130, 70 + 20 * i);
					g.drawString(itemName2[i] + "점대: " + Math.round(arcAngle2[i] * 100. / 360.) + "%", 330, 70 + 20 * i);
					g.drawString(itemName3[i] + "학과: " + Math.round(arcAngle3[i] * 100. / 360.) + "%", 530, 70 + 20 * i);
					//System.out.println(itemName[i] + " " + Math.round(arcAngle[i] * 100. / 360.) + "%");
					//System.out.println(itemName2[i] + " " + Math.round(arcAngle2[i] * 100. / 360.) + "%");
					//System.out.println(itemName3[i] + " " + Math.round(arcAngle3[i] * 100. / 360.) + "%");
				}
				
				//파이차트 그리기
				for (int i = 0; i < 4; i++) {
					//System.out.println("painting");
					//System.out.println("startAngle: "+startAngle);
					//System.out.println("startAngle2: "+startAngle2);
					//System.out.println("startAngle3: "+startAngle3);
					g.setColor(color[i]);
					g.fillArc(60, 190, 200, 200, startAngle, arcAngle[i]);
					g.fillArc(280, 190, 200, 200, startAngle2, arcAngle2[i]);
					g.fillArc(500, 190, 200, 200, startAngle3, arcAngle3[i]);
					startAngle = startAngle + arcAngle[i];
					startAngle2 = startAngle2 + arcAngle2[i];
					startAngle3 = startAngle3 + arcAngle3[i];
				}
				
				
			}
		}

	}

	/**********************************************************************************/
	//성적을 처리하는 클래스
	public class Grade {
		//과목의 평균이 저장될 변수
		private float sub1Aver, sub2Aver, sub3Aver, sub4Aver;
		//최고점 변수
		private int High1, High2, High3, High4;
		//최하점 변수
		private int Low1, Low2, Low3, Low4;
		//중간값 변수
		private int Mid1, Mid2, Mid3, Mid4;
		//등수 변수
		private int rank1, rank2, rank3, rank4;
		
		private int total1, total2, total3, total4;
		//"A","B","C" 와같은 학점이 저장될 변수
		private String grade1, grade2, grade3, grade4;

		private int[][] gradeArray1;
		private int[][] gradeArray2;
		private int[][] gradeArray3;
		private int[][] gradeArray4;

		String columnNames[] = { "석차", "학과", "학번", "이름", "전화번호", "Java", "소분설", "컴구", "객프", "평점" };
		QuickSort quick = new QuickSort();

		public Grade(DefaultTableModel targetModel) {

			// JTable jtable=new JTable(targetModel);

			setSub1Aver(targetModel);
			setSub2Aver(targetModel);
			setSub3Aver(targetModel);
			setSub4Aver(targetModel);

			setGradeArray1(targetModel);
			setGradeArray2(targetModel);
			setGradeArray3(targetModel);
		try {	setGradeArray4(targetModel);}catch(NumberFormatException e) {}

		}

		public int getTotal1() {
			return total1;
		}

		public int getTotal2() {
			return total2;
		}

		public int getTotal3() {
			return total3;
		}

		public int getTotal4() {
			return total4;
		}
		
		//등수 가져오기
		public void setRank1(int score) {
			for (int i = getTotal1(); i >= 1; i--) {
				if (score == gradeArray1[i - 1][0]) {
					this.rank1 = gradeArray1[i - 1][1];
					break;
				}
			}
		}

		public int getRank1() {
			return rank1;
		}

		public void setRank2(int score) {
			for (int i = getTotal2(); i >= 1; i--) {
				if (score == gradeArray2[i - 1][0]) {
					this.rank2 = gradeArray2[i - 1][1];
					break;
				}
			}
		}

		public int getRank2() {
			return rank2;
		}

		public void setRank3(int score) {
			for (int i = getTotal3(); i >= 1; i--) {
				if (score == gradeArray3[i - 1][0]) {
					this.rank3 = gradeArray3[i - 1][1];
					break;
				}
			}
		}

		public int getRank3() {
			return rank3;
		}

		public void setRank4(int score) {
			for (int i = getTotal4(); i >= 1; i--) {
				if (score == gradeArray4[i - 1][0]) {
					this.rank4 = gradeArray4[i - 1][1];
					break;
				}
			}
		}

		public int getRank4() {
			return rank4;
		}

		//배열에 값을 저장 후 최고점, 최하점, 중간값 계산
		public void setGradeArray1(DefaultTableModel targetModel) {

			int[] gradeArray1 = new int[targetModel.getRowCount()];
			int remove = 0;
			int[] index = new int[20];
			for (int i = 0; i < targetModel.getRowCount(); i++) {

				if (targetModel.getValueAt(i, 5).toString().equals(" ")) {

					index[remove] = i;
					remove++;
					continue;
				}
				gradeArray1[i] = (Integer.parseInt(targetModel.getValueAt(i, 5).toString()));
			}
			quick.sort(gradeArray1, 0, gradeArray1.length - 1);

			// System.out.print(gradeArray1[4]);

			this.gradeArray1 = new int[targetModel.getRowCount() - remove][2];

			int total1 = gradeArray1.length;
			this.total1 = total1;

			int disregard = 0;
			int change_index;
			for (int i = 0; i < gradeArray1.length; i++) {
				for (int j = 0; j < remove; j++) {
					if (i == index[j]) {
						disregard++;
						continue;

					}
				}

				if (disregard >= 1) {
					change_index = i - disregard;
					this.gradeArray1[change_index][0] = gradeArray1[i];
					this.gradeArray1[change_index][1] = total1--;
				} else {
					this.gradeArray1[i][0] = gradeArray1[i];
					this.gradeArray1[i][1] = total1--;
				}

				
			}

			this.High1 = this.gradeArray1[gradeArray1.length - remove - 1][0];
			this.Low1 = this.gradeArray1[0][0];
			if ((gradeArray1.length - remove) % 2 == 1) {
				this.Mid1 = this.gradeArray1[(gradeArray1.length - remove) / 2 + 1][0];
			} else
				this.Mid1 = this.gradeArray1[(gradeArray1.length - remove) / 2][0];

		}

		public int getHigh1() {
			return High1;
		}

		public int getLow1() {
			return Low1;
		}

		public int getMid1() {
			return Mid1;
		}

		public String getGradeArray1() {
			
			return gradeArray1[0] + "";
		}

		public void setGradeArray2(DefaultTableModel targetModel) {

			int[] gradeArray2 = new int[targetModel.getRowCount()];
			int remove = 0;
			int[] index = new int[20];
			for (int i = 0; i < targetModel.getRowCount(); i++) {
				if (targetModel.getValueAt(i, 6).toString().equals(" ")) {

					index[remove] = i;
					remove++;
					continue;
				}
				gradeArray2[i] = (Integer.parseInt(targetModel.getValueAt(i, 6).toString()));
			}

			quick.sort(gradeArray2, 0, gradeArray2.length - 1);

			this.gradeArray2 = new int[targetModel.getRowCount() - remove][2];

			int total2 = gradeArray2.length;
			this.total2 = total2;

			int disregard = 0;
			int change_index;

			for (int i = 0; i < gradeArray2.length; i++) {
				for (int j = 0; j < remove; j++) {
					if (i == index[j]) {
						disregard++;
						continue;
					}
				}
				if (disregard >= 1) {
					change_index = i - disregard;
					this.gradeArray2[change_index][0] = gradeArray2[i];
					this.gradeArray2[change_index][1] = total2--;
				} else {
					this.gradeArray2[i][0] = gradeArray2[i];
					this.gradeArray2[i][1] = total2--;
				} // 등수
					
			}

			this.High2 = this.gradeArray2[gradeArray2.length - 1 - remove][0];
			this.Low2 = this.gradeArray2[0][0];
			if ((gradeArray2.length - remove) % 2 == 1) {
				this.Mid2 = this.gradeArray2[(gradeArray2.length - remove) / 2 + 1][0];
			} else
				this.Mid2 = this.gradeArray2[(gradeArray2.length - remove) / 2][0];

		}

		public String getGradeArray2() {
			return gradeArray2[0] + "";
		}

		public int getHigh2() {
			return High2;
		}

		public int getLow2() {
			return Low2;
		}

		public int getMid2() {
			return Mid2;
		}

		public void setGradeArray3(DefaultTableModel targetModel) {

			int[] gradeArray3 = new int[targetModel.getRowCount()];
			int remove = 0;
			int[] index = new int[20];
			for (int i = 0; i < targetModel.getRowCount(); i++) {
				if (targetModel.getValueAt(i, 7).toString().equals(" ")) {

					index[remove] = i;
					remove++;
					continue;
				}
				gradeArray3[i] = (Integer.parseInt(targetModel.getValueAt(i, 7).toString()));
			}
			quick.sort(gradeArray3, 0, gradeArray3.length - 1);

			this.gradeArray3 = new int[targetModel.getRowCount() - remove][2];

			int total3 = gradeArray3.length;
			this.total3 = total3;

			int disregard = 0;
			int change_index;

			for (int i = 0; i < gradeArray3.length; i++) {
				for (int j = 0; j < remove; j++) {
					if (i == index[j]) {

						disregard++;
						continue;
					}
				}
				if (disregard >= 1) {
					change_index = i - disregard;
					this.gradeArray3[change_index][0] = gradeArray3[i];
					this.gradeArray3[change_index][1] = total3--;
				} else {
					this.gradeArray3[i][0] = gradeArray3[i];
					this.gradeArray3[i][1] = total3--;
				}

			}

			this.High3 = this.gradeArray3[gradeArray3.length - remove - 1][0];
			this.Low3 = this.gradeArray3[0][0];
			if ((gradeArray3.length - remove) % 2 == 1) {
				this.Mid3 = this.gradeArray3[(gradeArray3.length - remove) / 2 + 1][0];
			} else
				this.Mid3 = this.gradeArray3[(gradeArray3.length - remove) / 2][0];

		}

		public String getGradeArray3() {

			return gradeArray3[0] + "";
		}

		public int getHigh3() {
			return High3;
		}

		public int getLow3() {
			return Low3;
		}

		public int getMid3() {
			return Mid3;
		}

		public void setGradeArray4(DefaultTableModel targetModel) {

			int[] gradeArray4 = new int[targetModel.getRowCount()];
			int remove = 0;
			int[] index = new int[20];
			for (int i = 0; i < targetModel.getRowCount(); i++) {
				if (targetModel.getValueAt(i, 8).toString().equals(" ")) {

					index[remove] = i;
					remove++;
					continue;
				}
			try {	gradeArray4[i] = (Integer.parseInt(targetModel.getValueAt(i, 8).toString()));}catch(NumberFormatException e) {}
			}
			quick.sort(gradeArray4, 0, gradeArray4.length - 1);

			this.gradeArray4 = new int[targetModel.getRowCount() - remove][2];

			int total4 = gradeArray4.length;
			this.total4 = total4;

			int disregard = 0;
			int change_index;

			for (int i = 0; i < gradeArray4.length; i++) {
				for (int j = 0; j < remove; j++) {
					if (i == index[j]) {
						disregard++;
						continue;
					}
				}
				if (disregard >= 1) {
					change_index = i - disregard;
					this.gradeArray4[change_index][0] = gradeArray4[i];
					this.gradeArray4[change_index][1] = total4--;
				} else {
					this.gradeArray4[i][0] = gradeArray4[i];
					this.gradeArray4[i][1] = total4--;
				}
			}

			this.High4 = this.gradeArray4[gradeArray4.length - remove - 1][0];
			this.Low4 = this.gradeArray4[0][0];
			if ((gradeArray4.length - remove) % 2 == 1) {
				this.Mid4 = this.gradeArray4[(gradeArray4.length - remove) / 2 + 1][0];
			} else
				this.Mid4 = this.gradeArray4[(gradeArray4.length - remove) / 2][0];

		}

		public String getGradeArray4() {
			
			return gradeArray4[0] + "";
		}

		public int getHigh4() {
			return High4;
		}

		public int getLow4() {
			return Low4;
		}

		public int getMid4() {
			return Mid4;
		}

		//값을 테이블에서 가져와 형변환 후 평균 구하고 set
		public void setSub1Aver(DefaultTableModel targetModel) {
			float sum = 0;
			int remove = 0;
			for (int i = 0; i < targetModel.getRowCount(); i++) {
				String s = targetModel.getValueAt(i, 5) + "";
				if (s.equals(" ")) {
					remove++;
					return;
				}
				sum = sum + (Float.parseFloat(s));

			}
			float aver1 = sum / (targetModel.getRowCount() - remove);
			
			this.sub1Aver = aver1;
		}


		public void setSub2Aver(DefaultTableModel targetModel) {
			float sum = 0;
			int remove = 0;
			for (int i = 0; i < targetModel.getRowCount(); i++) {
				String s = targetModel.getValueAt(i, 6) + "";
				if (s.equals(" ")) {
					remove++;
					return;
				}
				sum = sum + (Float.parseFloat(s));
			}
			float aver2 = sum / (targetModel.getRowCount() - remove);
			// System.out.println(aver2);
			this.sub2Aver = aver2;
		}


		public void setSub3Aver(DefaultTableModel targetModel) {
			float sum = 0;
			int remove = 0;
			for (int i = 0; i < targetModel.getRowCount(); i++) {
				String s = targetModel.getValueAt(i, 7) + "";
				if (s.equals(" ")) {
					remove++;
					return;
				}
				sum = sum + (Float.parseFloat(s));
			}
			float aver3 = sum / (targetModel.getRowCount() - remove);
			// System.out.println(aver3);
			this.sub3Aver = aver3;
		}

	
		public void setSub4Aver(DefaultTableModel targetModel) {
			float sum = 0;
			int remove = 0;
			for (int i = 0; i < targetModel.getRowCount(); i++) {
				String s = targetModel.getValueAt(i, 8) + "";
				if (s.equals(" ")) {
					remove++;
					return;
				}
				sum = sum + (Float.parseFloat(s));
			}
			float aver4 = sum / (targetModel.getRowCount() - remove);
			// System.out.println(aver4);
			this.sub4Aver = aver4;
		}
		//평균 반환해주는 함수
		public String getSub1Aver() {
			return sub1Aver + "";
		}
		public String getSub2Aver() {
			return sub2Aver + "";
		}
		public String getSub3Aver() {
			return sub3Aver + "";
		}
		public String getSub4Aver() {
			return sub4Aver + "";
		}

		//점수에 따른 학점 계산 -> 절대평가 
		void setAbsoluteGrade(int index) {
			
			int rows = rankTable.tablemodel.getRowCount();

			String g1 = rankTable.tablemodel.getValueAt(index, 5) + "";
			String g2 = rankTable.tablemodel.getValueAt(index, 6) + "";
			String g3 = rankTable.tablemodel.getValueAt(index, 7) + "";
			String g4 = rankTable.tablemodel.getValueAt(index, 8) + "";

			int int_g1 = Integer.parseInt(g1);
			int int_g2 = Integer.parseInt(g2);
			int int_g3 = Integer.parseInt(g3);
			int int_g4 = Integer.parseInt(g4);

			if (int_g1 >= 90)
				grade1 = "A";
			else if (int_g1 < 90 && int_g1 >= 80)
				grade1 = "B";
			else if (int_g1 < 80 && int_g1 >= 70)
				grade1 = "C";
			else
				grade1 = "D";

			if (int_g2 >= 90)
				grade2 = "A";
			else if (int_g2 < 90 && int_g2 >= 80)
				grade2 = "B";
			else if (int_g2 < 80 && int_g2 >= 70)
				grade2 = "C";
			else
				grade2 = "D";

			if (int_g3 >= 90)
				grade3 = "A";
			else if (int_g3 < 90 && int_g3 >= 80)
				grade3 = "B";
			else if (int_g3 < 80 && int_g3 >= 70)
				grade3 = "C";
			else
				grade3 = "D";

			if (int_g4 >= 90)
				grade4 = "A";
			else if (int_g4 < 90 && int_g4 >= 80)
				grade4 = "B";
			else if (int_g4 < 80 && int_g4 >= 70)
				grade4 = "C";
			else
				grade4 = "D";

		}
		//점수에 따른 학점 계산 -> 상대평가
		void setGrade(int index) { // 학생 배열의 행의 index
			int rows = rankTable.tablemodel.getRowCount();

			String g1 = rankTable.tablemodel.getValueAt(index, 5) + "";
			String g2 = rankTable.tablemodel.getValueAt(index, 6) + "";
			String g3 = rankTable.tablemodel.getValueAt(index, 7) + "";
			String g4 = rankTable.tablemodel.getValueAt(index, 8) + "";

			int int_g1 = Integer.parseInt(g1);
			int int_g2 = Integer.parseInt(g2);
			int int_g3 = Integer.parseInt(g3);
			int int_g4 = Integer.parseInt(g4);

			int place = 0;

			// A,B,C학점 을 받는 학생수, 나머지는 D
			int As = (int) (gradePercent[0] * 0.01 * rows);
			int Bs = (int) (gradePercent[1] * 0.01 * rows);
			int Cs = (int) (gradePercent[2] * 0.01 * rows);
		//	System.out.println("As: " + As + ", Bs: " + Bs + ", Cs: " + Cs);

			setRank1(int_g1);
			place = getRank1();

			if (As >= place)
				grade1 = "A";
			else if (As + Bs >= place)
				grade1 = "B";
			else if (As + Bs + Cs >= place)
				grade1 = "C";
			else
				grade1 = "D";

			setRank2(int_g2);
			place = getRank2();

			if (As >= place)
				grade2 = "A";
			else if (As + Bs >= place)
				grade2 = "B";
			else if (As + Bs + Cs >= place)
				grade2 = "C";
			else
				grade2 = "D";

			setRank3(int_g3);
			place = getRank3();

			if (As >= place)
				grade3 = "A";
			else if (As + Bs >= place)
				grade3 = "B";
			else if (As + Bs + Cs >= place)
				grade3 = "C";
			else
				grade3 = "D";

			setRank4(int_g4);
			place = getRank4();

			if (As >= place)
				grade4 = "A";
			else if (As + Bs >= place)
				grade4 = "B";
			else if (As + Bs + Cs >= place)
				grade4 = "C";
			else
				grade4 = "D";
		}
		
		//학점을 반환
		public String getGrade1() {
			return grade1;
		}

		public String getGrade2() {
			return grade2;
		}

		public String getGrade3() {
			return grade3;
		}

		public String getGrade4() {
			return grade4;
		}

	}

	/**********************************************************************************/

	static public void main(String[] arg) {
		new Frame2();
	}
}
