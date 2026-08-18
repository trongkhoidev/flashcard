import json
import os

DB_FILE = "app/src/main/assets/vocab_data.json"

decks = [
    {"id": "en_health_beginner", "languageCode": "en", "title": "Sức khoẻ", "subtitle": "Sức khoẻ - Beginner", "iconEmoji": "⚕️", "level": "Beginner", "colorHex": "#EF4444", "cardCount": 30},
    {"id": "en_health_intermediate", "languageCode": "en", "title": "Sức khoẻ", "subtitle": "Sức khoẻ - Intermediate", "iconEmoji": "⚕️", "level": "Intermediate", "colorHex": "#DC2626", "cardCount": 30},
    {"id": "en_health_advanced", "languageCode": "en", "title": "Sức khoẻ", "subtitle": "Sức khoẻ - Advanced", "iconEmoji": "⚕️", "level": "Advanced", "colorHex": "#B91C1C", "cardCount": 30},
    {"id": "en_health_expert", "languageCode": "en", "title": "Sức khoẻ", "subtitle": "Sức khoẻ - Expert", "iconEmoji": "⚕️", "level": "Expert", "colorHex": "#991B1B", "cardCount": 30}
]

vocab_data = [
    # Beginner (0)
    ("Doctor", "/ˈdɒk.tər/", "noun", "The doctor is very kind.", "Bác sĩ", "Bác sĩ rất tử tế.", "Người khám bệnh", 0),
    ("Nurse", "/nɜːs/", "noun", "The nurse helps the patient.", "Y tá", "Y tá giúp đỡ bệnh nhân.", "Người chăm bệnh", 0),
    ("Hospital", "/ˈhɒs.pɪ.təl/", "noun", "He works at the hospital.", "Bệnh viện", "Anh ấy làm việc ở bệnh viện.", "Nơi chữa bệnh", 0),
    ("Clinic", "/ˈklɪn.ɪk/", "noun", "I went to a dental clinic.", "Phòng khám", "Tôi đã đến phòng khám nha khoa.", "Nơi khám nhỏ", 0),
    ("Medicine", "/ˈmed.ɪ.sən/", "noun", "Did you take your medicine?", "Thuốc", "Bạn đã uống thuốc chưa?", "Đồ trị bệnh", 0),
    ("Pill", "/pɪl/", "noun", "Take one pill a day.", "Viên thuốc", "Uống một viên mỗi ngày.", "Viên nén", 0),
    ("Sick", "/sɪk/", "adjective", "I feel sick today.", "Ốm, bệnh", "Hôm nay tôi thấy ốm.", "Cảm thấy không khỏe", 0),
    ("Ill", "/ɪl/", "adjective", "He is seriously ill.", "Bệnh", "Ông ấy bị bệnh nặng.", "Mắc bệnh", 0),
    ("Health", "/helθ/", "noun", "Health is important.", "Sức khoẻ", "Sức khỏe là quan trọng.", "Tình trạng cơ thể", 0),
    ("Pain", "/peɪn/", "noun", "I have a pain in my leg.", "Sự đau đớn", "Tôi bị đau ở chân.", "Cảm giác đau", 0),
    ("Hurt", "/hɜːt/", "verb", "My arm hurts.", "Đau, làm đau", "Cánh tay tôi bị đau.", "Gây đau đớn", 0),
    ("Blood", "/blʌd/", "noun", "There is blood on it.", "Máu", "Có vết máu trên đó.", "Chất lỏng màu đỏ", 0),
    ("Body", "/ˈbɒd.i/", "noun", "Keep your body healthy.", "Cơ thể", "Giữ cơ thể bạn khỏe mạnh.", "Thân thể", 0),
    ("Head", "/hed/", "noun", "My head is spinning.", "Đầu", "Đầu tôi đang quay cuồng.", "Phần trên cùng", 0),
    ("Heart", "/hɑːt/", "noun", "My heart beats fast.", "Tim", "Tim tôi đập nhanh.", "Quả tim", 0),
    ("Cold", "/kəʊld/", "noun", "I caught a cold.", "Cảm lạnh", "Tôi đã bị cảm lạnh.", "Bị cảm", 0),
    ("Cough", "/kɒf/", "noun", "He has a bad cough.", "Ho", "Anh ấy bị ho nặng.", "Tiếng ho", 0),
    ("Fever", "/ˈfiː.vər/", "noun", "She has a high fever.", "Sốt", "Cô ấy bị sốt cao.", "Nhiệt độ cơ thể cao", 0),
    ("Sleep", "/sliːp/", "verb", "You need to sleep more.", "Ngủ", "Bạn cần ngủ nhiều hơn.", "Nhắm mắt nghỉ ngơi", 0),
    ("Rest", "/rest/", "verb", "Please rest well.", "Nghỉ ngơi", "Hãy nghỉ ngơi thật tốt.", "Nằm nghỉ", 0),
    ("Cure", "/kjʊər/", "verb", "Can you cure this?", "Chữa trị", "Bạn có thể chữa khỏi cái này không?", "Làm cho khỏi", 0),
    ("Heal", "/hiːl/", "verb", "The cut will heal soon.", "Lành lại", "Vết cắt sẽ sớm lành lại.", "Phục hồi", 0),
    ("Weak", "/wiːk/", "adjective", "I feel very weak.", "Yếu", "Tôi cảm thấy rất yếu.", "Không có sức", 0),
    ("Strong", "/strɒŋ/", "adjective", "He is strong again.", "Khỏe mạnh", "Anh ấy đã khỏe lại.", "Khỏe khoắn", 0),
    ("Eye", "/aɪ/", "noun", "My eye is itchy.", "Mắt", "Mắt tôi bị ngứa.", "Dùng để nhìn", 0),
    ("Ear", "/ɪər/", "noun", "My ear hurts.", "Tai", "Tai tôi bị đau.", "Dùng để nghe", 0),
    ("Tooth", "/tuːθ/", "noun", "My tooth is broken.", "Răng", "Răng tôi bị gãy.", "Dùng để nhai", 0),
    ("Bone", "/bəʊn/", "noun", "He broke a bone.", "Xương", "Anh ấy bị gãy xương.", "Khung xương", 0),
    ("Skin", "/skɪn/", "noun", "Your skin is soft.", "Da", "Da của bạn thật mềm.", "Bề mặt cơ thể", 0),
    ("Diet", "/ˈdaɪ.ət/", "noun", "I am on a diet.", "Chế độ ăn kiêng", "Tôi đang ăn kiêng.", "Thực đơn hàng ngày", 0),

    # Intermediate (1)
    ("Disease", "/dɪˈziːz/", "noun", "A rare disease.", "Căn bệnh", "Một căn bệnh hiếm gặp.", "Bệnh tật", 1),
    ("Symptom", "/ˈsɪmp.təm/", "noun", "A symptom of the flu.", "Triệu chứng", "Một triệu chứng của cúm.", "Dấu hiệu bệnh", 1),
    ("Patient", "/ˈpeɪ.ʃənt/", "noun", "The patient is resting.", "Bệnh nhân", "Bệnh nhân đang nghỉ ngơi.", "Người bị bệnh", 1),
    ("Surgeon", "/ˈsɜː.dʒən/", "noun", "An expert surgeon.", "Bác sĩ phẫu thuật", "Một bác sĩ phẫu thuật chuyên gia.", "Người mổ", 1),
    ("Pharmacy", "/ˈfɑː.mə.si/", "noun", "Go to the pharmacy.", "Hiệu thuốc", "Hãy đến hiệu thuốc.", "Nơi bán thuốc", 1),
    ("Prescription", "/prɪˈskrɪp.ʃən/", "noun", "A prescription for antibiotics.", "Đơn thuốc", "Một đơn thuốc kháng sinh.", "Giấy kê thuốc", 1),
    ("Treatment", "/ˈtriːt.mənt/", "noun", "Medical treatment.", "Sự điều trị", "Điều trị y tế.", "Phương pháp chữa", 1),
    ("Virus", "/ˈvaɪə.rəs/", "noun", "A deadly virus.", "Vi-rút", "Một loại vi-rút chết người.", "Mầm bệnh siêu nhỏ", 1),
    ("Infection", "/ɪnˈfek.ʃən/", "noun", "A skin infection.", "Sự nhiễm trùng", "Nhiễm trùng da.", "Bị lây nhiễm", 1),
    ("Wound", "/wuːnd/", "noun", "Clean the wound.", "Vết thương", "Rửa sạch vết thương.", "Chỗ rách da", 1),
    ("Injury", "/ˈɪn.dʒər.i/", "noun", "A severe leg injury.", "Chấn thương", "Một chấn thương chân nghiêm trọng.", "Tổn thương", 1),
    ("Bleed", "/bliːd/", "verb", "His nose began to bleed.", "Chảy máu", "Mũi cậu ấy bắt đầu chảy máu.", "Rỉ máu", 1),
    ("Breathe", "/briːð/", "verb", "Breathe deeply.", "Thở", "Hãy thở sâu.", "Hít không khí", 1),
    ("Dizzy", "/ˈdɪz.i/", "adjective", "I feel dizzy.", "Chóng mặt", "Tôi cảm thấy chóng mặt.", "Hoa mắt", 1),
    ("Faint", "/feɪnt/", "verb", "She almost fainted.", "Ngất xỉu", "Cô ấy gần như ngất xỉu.", "Xỉu", 1),
    ("Vomit", "/ˈvɒm.ɪt/", "verb", "He wanted to vomit.", "Nôn mửa", "Anh ấy muốn nôn mửa.", "Ói", 1),
    ("Nausea", "/ˈnɔː.zi.ə/", "noun", "A feeling of nausea.", "Sự buồn nôn", "Cảm giác buồn nôn.", "Cảm giác muốn ói", 1),
    ("Allergy", "/ˈæl.ə.dʒi/", "noun", "A peanut allergy.", "Dị ứng", "Dị ứng đậu phộng.", "Cơ thể phản ứng", 1),
    ("Asthma", "/ˈæs.mə/", "noun", "He suffers from asthma.", "Hen suyễn", "Anh ấy bị hen suyễn.", "Bệnh khó thở", 1),
    ("Muscle", "/ˈmʌs.əl/", "noun", "Muscle pain.", "Cơ bắp", "Đau cơ bắp.", "Bắp thịt", 1),
    ("Stomach", "/ˈstʌm.ək/", "noun", "My stomach hurts.", "Dạ dày", "Dạ dày của tôi bị đau.", "Bụng", 1),
    ("Brain", "/breɪn/", "noun", "Brain damage.", "Não", "Tổn thương não.", "Bộ óc", 1),
    ("Lung", "/lʌŋ/", "noun", "Lung cancer.", "Phổi", "Ung thư phổi.", "Lá phổi", 1),
    ("Kidney", "/ˈkɪd.ni/", "noun", "Kidney failure.", "Thận", "Suy thận.", "Quả thận", 1),
    ("Liver", "/ˈlɪv.ər/", "noun", "Liver disease.", "Gan", "Bệnh gan.", "Lá gan", 1),
    ("Pulse", "/pʌls/", "noun", "Check his pulse.", "Mạch", "Kiểm tra mạch của anh ấy.", "Nhịp tim đập", 1),
    ("Pressure", "/ˈpreʃ.ər/", "noun", "High blood pressure.", "Huyết áp", "Huyết áp cao.", "Áp lực máu", 1),
    ("Bandage", "/ˈbæn.dɪdʒ/", "noun", "Apply a bandage.", "Băng gạc", "Dùng một miếng băng gạc.", "Băng cá nhân", 1),
    ("Injection", "/ɪnˈdʒek.ʃən/", "noun", "A lethal injection.", "Mũi tiêm", "Một mũi tiêm tử hình.", "Sự tiêm thuốc", 1),
    ("Vaccine", "/ˈvæk.siːn/", "noun", "The COVID-19 vaccine.", "Vắc-xin", "Vắc-xin COVID-19.", "Thuốc phòng bệnh", 1),

    # Advanced (2)
    ("Diagnosis", "/ˌdaɪ.əɡˈnəʊ.sɪs/", "noun", "An accurate diagnosis.", "Sự chẩn đoán", "Một sự chẩn đoán chính xác.", "Kết luận bệnh", 2),
    ("Therapy", "/ˈθer.ə.pi/", "noun", "Physical therapy.", "Liệu pháp", "Vật lý trị liệu.", "Phương pháp điều trị", 2),
    ("Physician", "/fɪˈzɪʃ.ən/", "noun", "Consult your physician.", "Bác sĩ đa khoa", "Hỏi ý kiến bác sĩ của bạn.", "Thầy thuốc", 2),
    ("Psychiatrist", "/saɪˈkaɪə.trɪst/", "noun", "See a psychiatrist.", "Bác sĩ tâm thần", "Gặp bác sĩ tâm thần.", "Bác sĩ tâm lý", 2),
    ("Pediatrician", "/ˌpiː.di.əˈtrɪʃ.ən/", "noun", "Take him to a pediatrician.", "Bác sĩ nhi khoa", "Đưa cậu bé đến bác sĩ nhi khoa.", "Bác sĩ trẻ em", 2),
    ("Obstetrician", "/ˌɒb.stəˈtrɪʃ.ən/", "noun", "She visits her obstetrician.", "Bác sĩ sản khoa", "Cô ấy đến thăm bác sĩ sản khoa của mình.", "Bác sĩ bà bầu", 2),
    ("Epidemic", "/ˌep.ɪˈdem.ɪk/", "noun", "A flu epidemic.", "Bệnh dịch", "Một đợt dịch cúm.", "Dịch bệnh bùng phát", 2),
    ("Pandemic", "/pænˈdem.ɪk/", "noun", "A global pandemic.", "Đại dịch", "Một đại dịch toàn cầu.", "Dịch lây lan thế giới", 2),
    ("Syndrome", "/ˈsɪn.drəʊm/", "noun", "Down syndrome.", "Hội chứng", "Hội chứng Down.", "Nhóm triệu chứng", 2),
    ("Chronic", "/ˈkrɒn.ɪk/", "adjective", "Chronic back pain.", "Mãn tính", "Đau lưng mãn tính.", "Kéo dài, khó chữa", 2),
    ("Acute", "/əˈkjuːt/", "adjective", "Acute appendicitis.", "Cấp tính", "Viêm ruột thừa cấp tính.", "Phát bệnh đột ngột", 2),
    ("Contagious", "/kənˈteɪ.dʒəs/", "adjective", "A highly contagious virus.", "Lây nhiễm", "Một loại vi-rút có tính lây nhiễm cao.", "Dễ lây cho người khác", 2),
    ("Immune", "/ɪˈmjuːn/", "adjective", "An immune system.", "Miễn dịch", "Một hệ miễn dịch.", "Sức đề kháng", 2),
    ("Malignant", "/məˈlɪɡ.nənt/", "adjective", "A malignant tumor.", "Ác tính", "Một khối u ác tính.", "Nguy hiểm đến tính mạng", 2),
    ("Benign", "/bɪˈnaɪn/", "adjective", "The tumor is benign.", "Lành tính", "Khối u là lành tính.", "Không gây ung thư", 2),
    ("Surgery", "/ˈsɜː.dʒər.i/", "noun", "Undergo heart surgery.", "Phẫu thuật", "Trải qua phẫu thuật tim.", "Mổ xẻ", 2),
    ("Transplant", "/ˈtræn.splɑːnt/", "noun", "A kidney transplant.", "Sự cấy ghép", "Một ca ghép thận.", "Ghép nội tạng", 2),
    ("Respiration", "/ˌres.pɪˈreɪ.ʃən/", "noun", "Artificial respiration.", "Sự hô hấp", "Hô hấp nhân tạo.", "Quá trình thở", 2),
    ("Digestion", "/daɪˈdʒes.tʃən/", "noun", "Good digestion.", "Sự tiêu hóa", "Sự tiêu hóa tốt.", "Phân giải thức ăn", 2),
    ("Metabolism", "/məˈtæb.əl.ɪ.zəm/", "noun", "A fast metabolism.", "Sự trao đổi chất", "Quá trình trao đổi chất nhanh.", "Chuyển hóa năng lượng", 2),
    ("Hormone", "/ˈhɔː.məʊn/", "noun", "Growth hormones.", "Nội tiết tố", "Nội tiết tố tăng trưởng.", "Chất kích thích", 2),
    ("Artery", "/ˈɑː.tər.i/", "noun", "A blocked artery.", "Động mạch", "Một động mạch bị tắc.", "Mạch máu từ tim", 2),
    ("Vein", "/veɪn/", "noun", "Blood in the vein.", "Tĩnh mạch", "Máu trong tĩnh mạch.", "Mạch máu về tim", 2),
    ("Fracture", "/ˈfræk.tʃər/", "noun", "A bone fracture.", "Sự gãy xương", "Một vết gãy xương.", "Vết nứt xương", 2),
    ("Concussion", "/kənˈkʌʃ.ən/", "noun", "Suffer a concussion.", "Chấn động não", "Bị chấn động não.", "Tổn thương sọ não", 2),
    ("Paralysis", "/pəˈræl.ə.sɪs/", "noun", "Partial paralysis.", "Sự tê liệt", "Bị liệt một phần.", "Mất khả năng cử động", 2),
    ("Inflammation", "/ˌɪn.fləˈmeɪ.ʃən/", "noun", "Reduce inflammation.", "Viêm", "Giảm viêm.", "Sưng, đỏ, đau", 2),
    ("Medication", "/ˌmed.ɪˈkeɪ.ʃən/", "noun", "Take your medication.", "Thuốc men", "Uống thuốc của bạn đi.", "Các loại thuốc", 2),
    ("Rehabilitation", "/ˌriː.həˌbɪl.ɪˈteɪ.ʃən/", "noun", "Drug rehabilitation.", "Sự phục hồi", "Phục hồi chức năng sau cai nghiện.", "Phục hồi chức năng", 2),
    ("Hygiene", "/ˈhaɪ.dʒiːn/", "noun", "Good personal hygiene.", "Vệ sinh", "Vệ sinh cá nhân tốt.", "Giữ gìn sạch sẽ", 2),

    # Expert (3)
    ("Epidemiology", "/ˌep.ɪˌdiː.miˈɒl.ə.dʒi/", "noun", "Study epidemiology.", "Dịch tễ học", "Nghiên cứu dịch tễ học.", "Khoa học dịch bệnh", 3),
    ("Oncology", "/ɒŋˈkɒl.ə.dʒi/", "noun", "Department of oncology.", "Ung thư học", "Khoa ung thư.", "Nghiên cứu ung thư", 3),
    ("Neurology", "/njʊəˈrɒl.ə.dʒi/", "noun", "He specializes in neurology.", "Thần kinh học", "Anh ấy chuyên về thần kinh học.", "Nghiên cứu não và dây thần kinh", 3),
    ("Cardiology", "/ˌkɑː.diˈɒl.ə.dʒi/", "noun", "Advanced cardiology.", "Tim mạch học", "Khoa tim mạch nâng cao.", "Nghiên cứu tim", 3),
    ("Dermatology", "/ˌdɜː.məˈtɒl.ə.dʒi/", "noun", "A clinic for dermatology.", "Da liễu học", "Một phòng khám da liễu.", "Nghiên cứu bệnh về da", 3),
    ("Anesthesia", "/ˌæn.əsˈθiː.zi.ə/", "noun", "Under general anesthesia.", "Thuốc mê", "Dưới tác dụng của thuốc mê toàn thân.", "Gây mất cảm giác", 3),
    ("Prognosis", "/prɒɡˈnəʊ.sɪs/", "noun", "A gloomy prognosis.", "Tiên lượng", "Một tiên lượng bi quan.", "Dự đoán bệnh", 3),
    ("Homeostasis", "/ˌhɒm.i.əʊˈsteɪ.sɪs/", "noun", "Maintain homeostasis.", "Sự cân bằng nội môi", "Duy trì sự cân bằng nội môi.", "Trạng thái ổn định", 3),
    ("Pathogen", "/ˈpæθ.ə.dʒən/", "noun", "A dangerous pathogen.", "Mầm bệnh", "Một mầm bệnh nguy hiểm.", "Vi khuẩn gây bệnh", 3),
    ("Antibody", "/ˈæn.tiˌbɒd.i/", "noun", "Antibody test.", "Kháng thể", "Xét nghiệm kháng thể.", "Chất chống bệnh", 3),
    ("Antigen", "/ˈæn.ti.dʒən/", "noun", "Detect the antigen.", "Kháng nguyên", "Phát hiện kháng nguyên.", "Chất kích thích miễn dịch", 3),
    ("Ischemia", "/ɪˈskiː.mi.ə/", "noun", "Cardiac ischemia.", "Thiếu máu cục bộ", "Thiếu máu cơ tim cục bộ.", "Thiếu máu cung cấp", 3),
    ("Aneurysm", "/ˈæn.jə.rɪ.zəm/", "noun", "Aortic aneurysm.", "Chứng phình động mạch", "Chứng phình động mạch chủ.", "Động mạch sưng phù", 3),
    ("Metastasis", "/məˈtæs.tə.sɪs/", "noun", "Prevent metastasis.", "Di căn", "Ngăn chặn sự di căn.", "Ung thư lan rộng", 3),
    ("Sepsis", "/ˈsep.sɪs/", "noun", "Severe sepsis.", "Nhiễm trùng máu", "Nhiễm trùng máu nghiêm trọng.", "Vi khuẩn vào máu", 3),
    ("Cirrhosis", "/sɪˈrəʊ.sɪs/", "noun", "Liver cirrhosis.", "Xơ gan", "Bệnh xơ gan.", "Gan bị tổn thương nặng", 3),
    ("Osteoporosis", "/ˌɒs.ti.əʊ.pəˈrəʊ.sɪs/", "noun", "Treat osteoporosis.", "Loãng xương", "Điều trị bệnh loãng xương.", "Xương giòn, dễ gãy", 3),
    ("Atherosclerosis", "/ˌæθ.ə.rəʊ.skləˈrəʊ.sɪs/", "noun", "Arterial atherosclerosis.", "Xơ vữa động mạch", "Bệnh xơ vữa động mạch.", "Mạch máu bị tắc nghẽn", 3),
    ("Dementia", "/dɪˈmen.ʃə/", "noun", "Elderly dementia.", "Chứng sa sút trí tuệ", "Chứng sa sút trí tuệ ở người già.", "Mất trí nhớ", 3),
    ("Schizophrenia", "/ˌskɪt.səˈfriː.ni.ə/", "noun", "Diagnosed with schizophrenia.", "Tâm thần phân liệt", "Được chẩn đoán mắc tâm thần phân liệt.", "Rối loạn tâm trí", 3),
    ("Placebo", "/pləˈsiː.bəʊ/", "noun", "The placebo effect.", "Giả dược", "Hiệu ứng giả dược.", "Thuốc không có tác dụng y học", 3),
    ("Autopsy", "/ˈɔː.tɒp.si/", "noun", "Perform an autopsy.", "Khám nghiệm tử thi", "Tiến hành khám nghiệm tử thi.", "Kiểm tra xác chết", 3),
    ("Biopsy", "/ˈbaɪ.ɒp.si/", "noun", "A tissue biopsy.", "Sinh thiết", "Một ca sinh thiết mô.", "Lấy mẫu xét nghiệm", 3),
    ("Endoscopy", "/enˈdɒs.kə.pi/", "noun", "Gastric endoscopy.", "Nội soi", "Nội soi dạ dày.", "Soi bên trong cơ thể", 3),
    ("Triage", "/ˈtriː.ɑːʒ/", "noun", "Medical triage.", "Phân loại bệnh nhân", "Việc phân loại bệnh nhân cấp cứu.", "Sắp xếp theo mức độ nặng", 3),
    ("Palliative", "/ˈpæl.i.ə.tɪv/", "adjective", "Palliative care.", "Giảm nhẹ", "Chăm sóc giảm nhẹ.", "Làm dịu cơn đau", 3),
    ("Prophylactic", "/ˌprɒf.ɪˈlæk.tɪk/", "adjective", "Prophylactic antibiotics.", "Phòng ngừa", "Kháng sinh dự phòng.", "Phòng tránh bệnh tật", 3),
    ("Immunosuppressant", "/ˌɪm.jʊ.nəʊ.səˈpres.ənt/", "noun", "Take immunosuppressant.", "Thuốc ức chế miễn dịch", "Uống thuốc ức chế miễn dịch.", "Ngừa phản ứng đào thải", 3),
    ("Defibrillator", "/diːˈfɪb.rɪ.leɪ.tər/", "noun", "Use a defibrillator.", "Máy khử rung tim", "Sử dụng máy khử rung tim.", "Máy kích tim", 3),
    ("Electrocardiogram", "/ɪˌlek.trəʊˈkɑː.di.ə.ɡræm/", "noun", "Read the electrocardiogram.", "Điện tâm đồ", "Đọc kết quả điện tâm đồ.", "Biểu đồ nhịp tim", 3)
]

level_map = ["en_health_beginner", "en_health_intermediate", "en_health_advanced", "en_health_expert"]

new_flashcards = []
for word, phonetic, pos, ex_en, meaning, ex_vi, tip, lvl in vocab_data:
    new_flashcards.append({
        "deckId": level_map[lvl],
        "languageCode": "en",
        "frontWord": word,
        "phonetic": phonetic,
        "partOfSpeech": pos,
        "frontExample": ex_en,
        "backMeaning": meaning,
        "backExampleTranslation": ex_vi,
        "memoryTip": tip
    })

if os.path.exists(DB_FILE):
    with open(DB_FILE, "r", encoding="utf-8") as f:
        data = json.load(f)
else:
    data = {"decks": [], "flashCards": []}

data["decks"].extend(decks)
data["flashCards"].extend(new_flashcards)

with open(DB_FILE, "w", encoding="utf-8") as f:
    json.dump(data, f, ensure_ascii=False, indent=2)

print("Successfully appended 120 words for Health (English) to DB!")
