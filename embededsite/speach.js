
const targetDiv = document.getElementById('lights-container');

const observer = new MutationObserver((mutationsList) => {
    for (const mutation of mutationsList) {
         handleLightsUpdate();
    }
});

// Options to watch: child elements, attributes, and text content
observer.observe(targetDiv, {
    childList: true,     // Detect when children are added/removed
    subtree: true,       // Watch nested children too
    attributes: true,    // Detect attribute changes
    characterData: true  // Detect text changes
});
const commandsArray=[]
const handleLightsUpdate =()=>{
    const container = document.getElementById('lights-container');

    for (let child of container.children) {
    commandsArray.push(['turn '+child.getAttribute('data-name')+' on.','okay, Turning '+ child.getAttribute('data-name')+' on',child.getAttribute('data-pin'),'off']);
    commandsArray.push(['turn '+child.getAttribute('data-name')+' off.','okay, Turning '+ child.getAttribute('data-name')+' off',child.getAttribute('data-pin'),'on']);

    }
}


const numberWords = { 'one': '1', 'two': '2', 'three': '3', 'four': '4' };

const normalizeCommand = (cmd) => cmd.toLowerCase().replace(/\b(one|to|three|four)\b/g, (m) => numberWords[m]);

let selectedVoice;

const initVoice = () => {
    const voices = speechSynthesis.getVoices();
    selectedVoice = voices.find(voice => voice.lang === 'en-US') || voices[1];
};

const speakResponse = (response) => {
    const utterance = new SpeechSynthesisUtterance(response);
    utterance.voice = selectedVoice;
    utterance.rate = 1.0;
    utterance.pitch = 1.0;
    speechSynthesis.speak(utterance);
};

const sendCommand = (transcript) => {
    console.log("Raw transcript:", transcript);

    const cmd = commandsArray.find(pair => normalizeCommand(pair[0]) === transcript);
    if (cmd) {
        console.log(transcript)
        const button = document.querySelector(`button[data-pin="${cmd[2]}"]`);
        if (button) {
            if(button.classList.contains(cmd[3])){
                button.click();  // Triggers the button's click event
                speakResponse(cmd[1]);
            }else{
                speakResponse('already have.')
            }
        }
    } else {
        speakResponse("Sorry, I didn't get that!");
    }
};

const initRecognition = () => {
    const recognition = new (window.SpeechRecognition || window.webkitSpeechRecognition)();
    recognition.lang = 'en-US';
    recognition.continuous = true;
    recognition.interimResults = false;
    let state = false;

    mic_In.addEventListener('click', (e) => {
        e.preventDefault();
        state = !state;
        if (state) {
            recognition.start();
            setTimeout(() => {
                recognition.stop();
            }, 5000);
        } else {
            recognition.stop();
        }
    });

    recognition.onresult = (event) => {
        const transcript = normalizeCommand(event.results[event.results.length - 1][0].transcript);
        sendCommand(transcript);
    };

    recognition.onerror = (event) => alert(`Speech recognition error: ${event.error}`);
};

const handleSubmit = (e) => {
    e.preventDefault();
    const transcript = normalizeCommand(textCommand.value);
    sendCommand(transcript);
    textCommand.value = '';
};

if ('SpeechRecognition' in window || 'webkitSpeechRecognition' in window && 'speechSynthesis' in window) {
    speechSynthesis.onvoiceschanged = initVoice;
    initVoice();

    initRecognition();
} else {
    alert('Not supported in this browser!');
} 